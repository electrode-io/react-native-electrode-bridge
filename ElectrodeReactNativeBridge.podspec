Pod::Spec.new do |s|
  s.name         = "ElectrodeReactNativeBridge"
  s.version      = "1.2.7"
  s.summary      = "React Native Electrode Bridge"

  s.authors      = { "Cody Garvin" => "cgarvin@walmartlabs.com" }
  s.homepage     = "https://gecgithub01.walmart.com/Electrode-Mobile-Platform/react-native-electrode-bridge"
  s.license      = "MIT"
  s.platform     = :ios, "8.0"

  s.source       = { :git => "https://gecgithub01.walmart.com/Electrode-Mobile-Platform/react-native-electrode-bridge" }
  s.source_files  = "ios/ElectrodeReactNativeBridge/*.{h,m}"

  s.dependency 'React'
end
