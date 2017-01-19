//
//  ElectrodeBridgeExampleViewController.m
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/6/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import "ElectrodeBridgeExampleViewController.h"
#import "RNNativeViewcontroller.h"
#import "RCTBundleURLProvider.h"
#import "RCTRootView.h"


@interface ElectrodeBridgeExampleViewController ()
@property (nonatomic, strong) RNNativeViewController *nativeController;
@property (nonatomic, strong) RCTRootView *reactNativeView;
@end

@implementation ElectrodeBridgeExampleViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
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

- (void)loadView {
  [super loadView];
  
  NSURL *jsCodeLocation;
  
  jsCodeLocation = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index.ios" fallbackResource:nil];
  
  self.reactNativeView = [[RCTRootView alloc] initWithBundleURL:jsCodeLocation
                                                     moduleName:@"ElectrodeBridgeExample"
                                              initialProperties:nil
                                                  launchOptions:nil];
  
  _reactNativeView.backgroundColor = [[UIColor alloc] initWithRed:1.0f green:1.0f blue:1.0f alpha:1];

 
  // Add the views that will split our two sections (native and rn)
  self.nativeController = [[RNNativeViewController alloc] init];
  [self addChildViewController:_nativeController];
  [self.view addSubview:self.nativeController.view];
  [_nativeController didMoveToParentViewController:self];
  
  self.nativeController.view.translatesAutoresizingMaskIntoConstraints = NO;
  [NSLayoutConstraint activateConstraints:@[[NSLayoutConstraint constraintWithItem:_nativeController.view attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeHeight multiplier:.5 constant:1.0]]];
  [NSLayoutConstraint activateConstraints:@[[NSLayoutConstraint constraintWithItem:_nativeController.view attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeWidth multiplier:1.0 constant:1.0]]];
  [NSLayoutConstraint activateConstraints:@[[NSLayoutConstraint constraintWithItem:_nativeController.view attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1.0 constant:1.0]]];
  [NSLayoutConstraint activateConstraints:@[[NSLayoutConstraint constraintWithItem:_nativeController.view attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeft multiplier:1.0 constant:1.0]]];
  
  [self.view addSubview:_reactNativeView];
  
  _reactNativeView.translatesAutoresizingMaskIntoConstraints = NO;
  [NSLayoutConstraint activateConstraints:@[[NSLayoutConstraint constraintWithItem:_reactNativeView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeHeight multiplier:.5 constant:1.0]]];
  [NSLayoutConstraint activateConstraints:@[[NSLayoutConstraint constraintWithItem:_reactNativeView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeWidth multiplier:1.0 constant:1.0]]];
  [NSLayoutConstraint activateConstraints:@[[NSLayoutConstraint constraintWithItem:_reactNativeView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:_nativeController.view attribute:NSLayoutAttributeBottom multiplier:1.0 constant:1.0]]];
  [NSLayoutConstraint activateConstraints:@[[NSLayoutConstraint constraintWithItem:_reactNativeView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeft multiplier:1.0 constant:1.0]]];
}
@end
