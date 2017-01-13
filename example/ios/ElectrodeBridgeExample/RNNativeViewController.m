//
//  RNNativeViewController.m
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/1/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <stdlib.h>

#import "RNNativeViewController.h"
#import "ElectrodeBridge.h"
#import "ElectrodeBridgeEvent.h"
#import "ElectrodeBridgeRequest.h"
#import "ElectrodeBridgeHolder.h"
#import "ElectrodeEventDispatcher.h"
#import "ElectrodeEventRegistrar.h"
#import "ElectrodeRequestRegistrar.h"
#import "ElectrodeRequestDispatcher.h"

typedef void (^RNNativeEventListenerBlock)(NSDictionary *data);
typedef void (^RNNativeRequestListenerErrorBlock)(NSString *code, NSString *message);
typedef void (^RNNativeRequestListenerBlock)(NSDictionary *data, id<ElectrodeRequestCompletioner> completioner);

////////////////////////////////////////////////////////////////////////////////
#pragma mark - RNNativeEventListener
@interface RNNativeEventListener : NSObject  <ElectrodeEventListener>
@property (nonatomic, copy) RNNativeEventListenerBlock eventListenerBlock;
@end

@implementation RNNativeEventListener

- (void)onEvent:(NSDictionary *)data
{
  self.eventListenerBlock(data);
}

@end

////////////////////////////////////////////////////////////////////////////////
#pragma mark - RNNativeRequestHandler
@interface RNNativeRequestHandler : NSObject <ElectrodeRequestHandler>
@property (nonatomic, copy) RNNativeRequestListenerBlock requestHitBlock;
@end

@implementation RNNativeRequestHandler

- (void)onRequest:(NSDictionary *)data requestCompletioner:(id<ElectrodeRequestCompletioner>)completioner
{
  NSLog(@"Time to do request: %@", data);
  // Show the buttons
  self.requestHitBlock(data, completioner);
}

@end

////////////////////////////////////////////////////////////////////////////////
#pragma mark - RNNativeRequestListener
@interface RNNativeRequestListener : NSObject <ElectrodeRequestCompletionListener>
- (instancetype)initWithSuccesBlock:(RNNativeEventListenerBlock)success errorBlock:(RNNativeRequestListenerErrorBlock)error;
@property (nonatomic, copy) RNNativeEventListenerBlock successBlock;
@property (nonatomic, copy) RNNativeRequestListenerErrorBlock errorBlock;
@end

@implementation RNNativeRequestListener

- (instancetype)initWithSuccesBlock:(RNNativeEventListenerBlock)success errorBlock:(RNNativeRequestListenerErrorBlock)error
{
  self = [super init];
  if (self)
  {
    self.successBlock = success;
    self.errorBlock = error;
  }
  return self;
}

- (void)onSuccess:(NSDictionary *)data
{
  self.successBlock(data);
}

- (void)onError:(NSString *)code message:(NSString *)message
{
  self.errorBlock(code, message);
}

@end

////////////////////////////////////////////////////////////////////////////////
#pragma mark - RNNativeViewController extension
@interface RNNativeViewController ()
@property (nonatomic, strong) UILabel *logLabel;
@property (nonatomic, strong) UISegmentedControl *requestControl;
@property (nonatomic, strong) UISegmentedControl *eventControl;
@property (nonatomic, strong) UIView *containerResolveView;
@property (nonatomic, strong) id<ElectrodeRequestCompletioner> requestCompletioner;
@end


@implementation RNNativeViewController

- (void)viewDidLoad {
  [super viewDidLoad];
  // Do any additional setup after loading the view.
  self.view.backgroundColor = [UIColor blackColor];
  
  ////
  // Add the event listener to the JS bridge
  [[ElectrodeBridgeHolder sharedInstance] registerStartupListener:^(ElectrodeBridge *bridge) {

    ////
    // Create an event listener so we know when events are sent to native
    RNNativeEventListener *eventListener = [[RNNativeEventListener alloc] init];
    __weak RNNativeViewController *weakSelf = self;
    eventListener.eventListenerBlock = ^(NSDictionary *data) {
      
      if ([data objectForKey:@"randFloat"])
      {
        weakSelf.logString = [NSString stringWithFormat:@"Event Received: randFloat=%@", [data objectForKey:@"randFloat"]];
      }
      else if ([data objectForKey:@"randInt"])
      {
        weakSelf.logString = [NSString stringWithFormat:@"Event Received: randFloat=%@", [data objectForKey:@"randInt"]];
      }
      else
      {
        weakSelf.logString = @"Event Received w/o data";
      }
    };
    [[ElectrodeBridgeHolder sharedInstance] registerEventListener:@"event.example" eventListener:eventListener];
    
    ////
    // Add the request listener to the bridge
    RNNativeRequestHandler *nativeRequestHandler = [[RNNativeRequestHandler alloc] init];
    nativeRequestHandler.requestHitBlock = ^(NSDictionary *data, id<ElectrodeRequestCompletioner> completioner){
      
      if ([data objectForKey:@"randFloat"])
      {
        weakSelf.logString = [NSString stringWithFormat:@"Request Received: randFloat=%@", [data objectForKey:@"randFloat"]];
      }
      else
      {
        weakSelf.logString = @"Request Received w/o data";
      }

      _containerResolveView.hidden = NO;
      weakSelf.requestCompletioner = completioner;
    };
    NSError *error = nil;
    [[ElectrodeBridgeHolder sharedInstance] registerRequestHandler:@"request.example" requestHandler:nativeRequestHandler error:&error];
    
    if (error)
    {
      // Event handler already exists
      NSLog(@"Event handler already exists, try again newb");
    }
  }];
  
  // Build up the view
  [self buildView];
}

- (void)didReceiveMemoryWarning {
  [super didReceiveMemoryWarning];
  // Dispose of any resources that can be recreated.
}

- (void)buildView {
  
  self.view.backgroundColor = [UIColor colorWithRed:0.192f green:0.301f blue:0.301f alpha:1.0f];
  
  // Add the send request event
  UIView *containerRequestView = [[UIView alloc] init];
  containerRequestView.backgroundColor = [UIColor colorWithRed:0.356f green:0.356f blue:0.356f alpha:1.0];
  [self.view addSubview:containerRequestView];
  
  UILabel *sendRequestLabel = [[UILabel alloc] init];
  sendRequestLabel.text = @"Send request";
  sendRequestLabel.font = [UIFont systemFontOfSize:14.0f];
  sendRequestLabel.textColor = [UIColor colorWithRed:0.807f green:0.69f blue:0.447f alpha:1.0f];
  [containerRequestView addSubview:sendRequestLabel];
  
  self.logLabel = [[UILabel alloc] init];
  self.logLabel.text = @"[NATIVE] >>> ";
  self.logLabel.textColor = [UIColor whiteColor];
  self.logLabel.font = [UIFont systemFontOfSize:11.0f];
  self.logLabel.backgroundColor = [UIColor colorWithRed:0.192f green:0.301f blue:0.301f alpha:1.0f];
  [self.view addSubview:_logLabel];
  
  self.requestControl = [[UISegmentedControl alloc] initWithItems:@[@"JS", @"Native"]];
  _requestControl.selectedSegmentIndex = 0;
  [_requestControl setTintColor:[UIColor colorWithRed:0.254f green:0.427f blue:0.858f alpha:1.0f]];
  [_requestControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateSelected];
  [_requestControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateNormal];
  [containerRequestView addSubview:_requestControl];
  
  UIButton *withRequestData = [[UIButton alloc] init];
  [withRequestData setTitle:@"with data" forState:UIControlStateNormal];
  [withRequestData setBackgroundColor:[UIColor colorWithRed:0.254f green:0.427f blue:0.858f alpha:1.0f]];
  [[withRequestData titleLabel] setFont:[UIFont systemFontOfSize:11.0f]];
  [withRequestData setTag:101];
  [withRequestData addTarget:self action:@selector(sendRequest:) forControlEvents:UIControlEventTouchUpInside];
  [containerRequestView addSubview:withRequestData];
  
  UIButton *withoutRequestData = [[UIButton alloc] init];
  [withoutRequestData setTitle:@"w/o data" forState:UIControlStateNormal];
  [withoutRequestData setBackgroundColor:[UIColor colorWithRed:0.254f green:0.427f blue:0.858f alpha:1.0f]];
  [[withoutRequestData titleLabel] setFont:[UIFont systemFontOfSize:11.0f]];
  [withoutRequestData setTag:102];
  [withoutRequestData addTarget:self action:@selector(sendRequest:) forControlEvents:UIControlEventTouchUpInside];
  [containerRequestView addSubview:withoutRequestData];
  
  _logLabel.translatesAutoresizingMaskIntoConstraints = NO;
  containerRequestView.translatesAutoresizingMaskIntoConstraints = NO;
  sendRequestLabel.translatesAutoresizingMaskIntoConstraints = NO;
  withRequestData.translatesAutoresizingMaskIntoConstraints = NO;
  withoutRequestData.translatesAutoresizingMaskIntoConstraints = NO;
  _requestControl.translatesAutoresizingMaskIntoConstraints = NO;
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[containerRequestView]|" options:0 metrics:nil views:@{@"containerRequestView":containerRequestView}]];
  
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-10-[sendRequestLabel]-(>=20)-[withRequestData(100)]-10-[withoutRequestData(100)]-10-|" options:NSLayoutFormatAlignAllCenterY metrics:nil views:NSDictionaryOfVariableBindings(sendRequestLabel, withoutRequestData, withRequestData)]];
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-10-[requestControl]-10-|" options:NSLayoutFormatAlignAllCenterY metrics:nil views:@{@"requestControl":_requestControl}]];

  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-10-[logLabel]-10-|" options:NSLayoutFormatAlignAllCenterY metrics:nil views:@{@"logLabel":_logLabel}]];
  
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-10-[sendRequestLabel]-10-[requestControl(30)]" options:0 metrics:nil views:@{@"sendRequestLabel":sendRequestLabel, @"requestControl": _requestControl}]];
  
  
  // Add the send event view
  UIView *containerEventView = [[UIView alloc] init];
  containerEventView.backgroundColor = [UIColor colorWithRed:0.356f green:0.356f blue:0.356f alpha:1.0];
  [self.view addSubview:containerEventView];
  
  UILabel *sendEventLabel = [[UILabel alloc] init];
  sendEventLabel.text = @"Send Event";
  sendEventLabel.font = [UIFont systemFontOfSize:14.0f];
  sendEventLabel.textColor = [UIColor colorWithRed:0.807f green:0.69f blue:0.447f alpha:1.0f];
  [containerEventView addSubview:sendEventLabel];
  
  self.eventControl = [[UISegmentedControl alloc] initWithItems:@[@"JS", @"Native", @"Global"]];
  _eventControl.selectedSegmentIndex = 0;
  [_eventControl setTintColor:[UIColor colorWithRed:0.254f green:0.427f blue:0.858f alpha:1.0f]];
  [_eventControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateSelected];
  [_eventControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateNormal];
  [containerEventView addSubview:_eventControl];
  
  UIButton *withEventData = [[UIButton alloc] init];
  [withEventData setTitle:@"with data" forState:UIControlStateNormal];
  [withEventData setBackgroundColor:[UIColor colorWithRed:0.254f green:0.427f blue:0.858f alpha:1.0f]];
  [[withEventData titleLabel] setFont:[UIFont systemFontOfSize:11.0f]];
  withEventData.tag = 201;
  [withEventData addTarget:self action:@selector(sendEvent:) forControlEvents:UIControlEventTouchUpInside];
  [containerEventView addSubview:withEventData];
  
  UIButton *withoutEventData = [[UIButton alloc] init];
  [withoutEventData setTitle:@"w/o data" forState:UIControlStateNormal];
  [withoutEventData setBackgroundColor:[UIColor colorWithRed:0.254f green:0.427f blue:0.858f alpha:1.0f]];
  [[withoutEventData titleLabel] setFont:[UIFont systemFontOfSize:11.0f]];
  withoutEventData.tag = 202;
  [withoutEventData addTarget:self action:@selector(sendEvent:) forControlEvents:UIControlEventTouchUpInside];
  [containerEventView addSubview:withoutEventData];
  
  containerEventView.translatesAutoresizingMaskIntoConstraints = NO;
  sendEventLabel.translatesAutoresizingMaskIntoConstraints = NO;
  withEventData.translatesAutoresizingMaskIntoConstraints = NO;
  withoutEventData.translatesAutoresizingMaskIntoConstraints = NO;
  _eventControl.translatesAutoresizingMaskIntoConstraints = NO;
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint
                                           constraintsWithVisualFormat:@"H:|[containerEventView]|"
                                           options:0
                                           metrics:nil
                                           views:@{@"containerEventView":containerEventView}]];
  
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint
                                           constraintsWithVisualFormat:@"H:|-10-[sendEventLabel]-(>=20)-[withEventData(100)]-10-[withoutEventData(100)]-10-|"
                                           options:NSLayoutFormatAlignAllCenterY
                                           metrics:nil
                                           views:NSDictionaryOfVariableBindings(sendEventLabel, withoutEventData, withEventData)]];
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint
                                           constraintsWithVisualFormat:@"H:|-10-[eventControl]-10-|"
                                           options:NSLayoutFormatAlignAllCenterY
                                           metrics:nil
                                           views:@{@"eventControl":_eventControl}]];
  
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint
                                           constraintsWithVisualFormat:@"V:|-10-[sendEventLabel]-10-[eventControl(30)]"
                                           options:0
                                           metrics:nil
                                           views:@{@"sendEventLabel":sendEventLabel, @"eventControl": _eventControl}]];

  
  // Add the resolve view
  self.containerResolveView = [[UIView alloc] init];
  _containerResolveView.backgroundColor = [UIColor colorWithRed:0.356f green:0.356f blue:0.356f alpha:1.0];
  _containerResolveView.hidden = YES;
  [self.view addSubview:_containerResolveView];
  
  UILabel *sendResolveLabel = [[UILabel alloc] init];
  sendResolveLabel.text = @"Resolve Request";
  sendResolveLabel.font = [UIFont systemFontOfSize:14.0f];
  sendResolveLabel.textColor = [UIColor colorWithRed:0.807f green:0.69f blue:0.447f alpha:1.0f];
  [_containerResolveView addSubview:sendResolveLabel];
  
  UIButton *withResolveData = [[UIButton alloc] init];
  [withResolveData setTitle:@"with data" forState:UIControlStateNormal];
  [withResolveData setBackgroundColor:[UIColor colorWithRed:0.254f green:0.427f blue:0.858f alpha:1.0f]];
  [[withResolveData titleLabel] setFont:[UIFont systemFontOfSize:11.0f]];
  [withResolveData setTag:301];
  [withResolveData addTarget:self action:@selector(handleRequest:) forControlEvents:UIControlEventTouchUpInside];
  [_containerResolveView addSubview:withResolveData];
  
  UIButton *withoutResolveData = [[UIButton alloc] init];
  [withoutResolveData setTitle:@"w/o data" forState:UIControlStateNormal];
  [withoutResolveData setBackgroundColor:[UIColor colorWithRed:0.254f green:0.427f blue:0.858f alpha:1.0f]];
  [withoutResolveData setTag:302];
  [withoutResolveData addTarget:self action:@selector(handleRequest:) forControlEvents:UIControlEventTouchUpInside];
  [[withoutResolveData titleLabel] setFont:[UIFont systemFontOfSize:11.0f]];
  [_containerResolveView addSubview:withoutResolveData];
  
  UILabel *rejectResolveLabel = [[UILabel alloc] init];
  rejectResolveLabel.text = @"Reject Request";
  rejectResolveLabel.font = [UIFont systemFontOfSize:14.0f];
  rejectResolveLabel.textColor = [UIColor colorWithRed:0.807f green:0.69f blue:0.447f alpha:1.0f];
  [_containerResolveView addSubview:rejectResolveLabel];
  
  UIButton *withoutRejectData = [[UIButton alloc] init];
  [withoutRejectData setTitle:@"w/o data" forState:UIControlStateNormal];
  [withoutRejectData setBackgroundColor:[UIColor colorWithRed:0.254f green:0.427f blue:0.858f alpha:1.0f]];
  [[withoutRejectData titleLabel] setFont:[UIFont systemFontOfSize:11.0f]];
  [withoutRejectData setTag:303];
  [withoutRejectData addTarget:self action:@selector(handleRequest:) forControlEvents:UIControlEventTouchUpInside];
  [_containerResolveView addSubview:withoutRejectData];

  
  _containerResolveView.translatesAutoresizingMaskIntoConstraints = NO;
  sendResolveLabel.translatesAutoresizingMaskIntoConstraints = NO;
  rejectResolveLabel.translatesAutoresizingMaskIntoConstraints = NO;
  withResolveData.translatesAutoresizingMaskIntoConstraints = NO;
  withoutResolveData.translatesAutoresizingMaskIntoConstraints = NO;
  withoutRejectData.translatesAutoresizingMaskIntoConstraints = NO;
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint
                                           constraintsWithVisualFormat:@"H:|[containerResolveView]|"
                                           options:0
                                           metrics:nil
                                           views:@{@"containerResolveView":_containerResolveView}]];
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint
                                           constraintsWithVisualFormat:@"V:|-20-[logLabel(40)][containerRequestView(80)]-10-[containerEventView(80)]-10-[containerResolveView]"
                                           options:0
                                           metrics:nil
                                           views:@{@"logLabel": _logLabel, @"containerRequestView": containerRequestView, @"containerEventView":containerEventView, @"containerResolveView": _containerResolveView}]];
  
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint
                                           constraintsWithVisualFormat:@"H:|-10-[sendResolveLabel]-(>=20)-[withResolveData(100)]-10-[withoutResolveData(100)]-10-|"
                                           options:NSLayoutFormatAlignAllCenterY
                                           metrics:nil
                                           views:NSDictionaryOfVariableBindings(sendResolveLabel, withoutResolveData, withResolveData)]];
  
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint
                                           constraintsWithVisualFormat:@"H:|-10-[rejectResolveLabel]-(>=20)-[withoutRejectData(100)]-10-|"
                                           options:NSLayoutFormatAlignAllCenterY
                                           metrics:nil
                                           views:NSDictionaryOfVariableBindings(rejectResolveLabel, withoutRejectData)]];
  
  [NSLayoutConstraint activateConstraints:[NSLayoutConstraint
                                           constraintsWithVisualFormat:@"V:|-10-[sendResolveLabel]-10-[rejectResolveLabel]->=10-|"
                                           options:0
                                           metrics:nil
                                           views:@{@"sendResolveLabel":sendResolveLabel, @"rejectResolveLabel": rejectResolveLabel}]];

}

- (void)setLogString:(NSString *)logString
{
  if (_logString != logString)
  {
    _logString = logString;

    _logLabel.text = [NSString stringWithFormat:@"[NATIVE] >>> %@", logString];
  }
}

- (void)sendEvent:(UIControl *)sender
{
  EBDispatchMode mode = JS;
  switch (_eventControl.selectedSegmentIndex)
  {
    case 0:
      mode = JS;
      break;
    case 1:
      mode = NATIVE;
      break;
    case 2:
      mode = GLOBAL;
      break;
  }
  
  NSDictionary *randomData = nil;
  if (sender.tag == 201)
  { // It's with data
    randomData = @{@"randFloat":@((float)rand() / RAND_MAX)};
  }
  
  [[ElectrodeBridgeHolder sharedInstance] registerStartupListener:^(ElectrodeBridge *bridge) {

    ElectrodeBridgeEvent *event = [[ElectrodeBridgeEvent alloc] initWithName:@"event.example" data:randomData  mode:mode];
    [bridge emitEvent:event];
  }];
  
}

- (void)sendRequest:(UIControl *)sender
{
  EBDispatchMode mode = JS;
  switch (_requestControl.selectedSegmentIndex)
  {
    case 0:
      mode = JS;
      break;
    case 1:
      mode = NATIVE;
      break;
  }
  
  // Create the random data needed to send the request
  NSDictionary *randomData = nil;
  if (sender.tag == 101)
  { // It's with data
    randomData = @{@"randFloat":@((float)rand() / RAND_MAX)};
  }
  
  
  __weak RNNativeViewController *weakSelf = self;
  [[ElectrodeBridgeHolder sharedInstance] registerStartupListener:^(ElectrodeBridge *bridge) {
    ElectrodeBridgeRequest *request = [[ElectrodeBridgeRequest alloc] initWithName:@"request.example" data:randomData mode:mode timeout:5];
    
    [bridge sendRequest:request completionListener:
     [[RNNativeRequestListener alloc] initWithSuccesBlock:
      ^(NSDictionary *data)
      {
        if ([data objectForKey:@"randFloat"])
        {
          weakSelf.logString = [NSString stringWithFormat:@"Request Handled: randFloat=%@", [data objectForKey:@"randFloat"]];
        }
        else
        {
          weakSelf.logString = @"Request Handled w/o data";
        }

      } errorBlock:
      ^(NSString *code, NSString *message)
      {
        weakSelf.logString = [NSString stringWithFormat:@"Request Rejected: %@: %@", code, message];
      }]];
  }];
}

- (void)handleRequest:(UIControl *)sender
{
  _containerResolveView.hidden = YES;
  
  if (sender.tag == 301)
  { // Resolve with data
    [self.requestCompletioner success:@{@"randFloat":@((float)rand() / RAND_MAX)}];
  }
  else if (sender.tag == 302)
  { // Resolve without data
    [self.requestCompletioner success];
  }
  else
  { // Reject without data
    [self.requestCompletioner error:@"-231" message:@"BROKEN!!"];
  }
}

@end


















