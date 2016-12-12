//
//  RNNativeViewController.m
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/1/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import "RNNativeViewController.h"
#import <UIKit/UIKit.h>

@interface RNNativeViewController ()
@property (nonatomic, strong) UILabel *logLabel;
@property (nonatomic, strong) UISegmentedControl *requestControl;
@property (nonatomic, strong) UISegmentedControl *eventControl;
@end

@implementation RNNativeViewController

- (void)viewDidLoad {
  [super viewDidLoad];
  // Do any additional setup after loading the view.
  self.view.backgroundColor = [UIColor blackColor];
  
  [self buildView];
}

- (void)didReceiveMemoryWarning {
  [super didReceiveMemoryWarning];
  // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (void)buildView {
  
  self.view.backgroundColor = [UIColor colorWithRed:0.192f green:0.301f blue:0.301f alpha:1.0f];
  
  UIView *containerRequestView = [[UIView alloc] init];
  containerRequestView.backgroundColor = [UIColor colorWithRed:0.356f green:0.356f blue:0.356f alpha:1.0];
  [self.view addSubview:containerRequestView];
  
  UILabel *sendRequestLabel = [[UILabel alloc] init];
  sendRequestLabel.text = @"Send request";
  sendRequestLabel.textColor = [UIColor colorWithRed:0.807f green:0.69f blue:0.447f alpha:1.0f];
  [containerRequestView addSubview:sendRequestLabel];
  
  self.logLabel = [[UILabel alloc] init];
  self.logLabel.text = @"[NATIVE] >> ";
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
  [containerRequestView addSubview:withRequestData];
  
  UIButton *withoutRequestData = [[UIButton alloc] init];
  [withoutRequestData setTitle:@"w/o data" forState:UIControlStateNormal];
  [withoutRequestData setBackgroundColor:[UIColor colorWithRed:0.254f green:0.427f blue:0.858f alpha:1.0f]];
  [[withoutRequestData titleLabel] setFont:[UIFont systemFontOfSize:11.0f]];
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
  
  
  
  UIView *containerEventView = [[UIView alloc] init];
  containerEventView.backgroundColor = [UIColor colorWithRed:0.356f green:0.356f blue:0.356f alpha:1.0];
  [self.view addSubview:containerEventView];
  
  UILabel *sendEventLabel = [[UILabel alloc] init];
  sendEventLabel.text = @"Send Event";
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
  [containerEventView addSubview:withEventData];
  
  UIButton *withoutEventData = [[UIButton alloc] init];
  [withoutEventData setTitle:@"w/o data" forState:UIControlStateNormal];
  [withoutEventData setBackgroundColor:[UIColor colorWithRed:0.254f green:0.427f blue:0.858f alpha:1.0f]];
  [[withoutEventData titleLabel] setFont:[UIFont systemFontOfSize:11.0f]];
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
                                           constraintsWithVisualFormat:@"V:|-20-[logLabel(40)][containerRequestView(80)]-10-[containerEventView(80)]"
                                           options:0
                                           metrics:nil
                                           views:@{@"logLabel": _logLabel, @"containerRequestView": containerRequestView, @"containerEventView":containerEventView}]];
  
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

}

- (void)setLogString:(NSString *)logString
{
  if (_logString != logString)
  {
    _logString = logString;
    
    _logLabel.text = [NSString stringWithFormat:@"[NATIVE] >>> %@", logString];
  }
}

@end
