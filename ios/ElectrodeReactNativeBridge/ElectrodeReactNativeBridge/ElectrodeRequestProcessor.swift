//
//  ElectrodeRequestProcessor.swift
//  ElectrodeReactNativeBridge
//
//  Created by Cody Garvin on 3/10/17.
//  Copyright © 2017 Bharath Marulasiddappa. All rights reserved.
//

import UIKit

class ElectrodeRequestProcessor<TReq: Bridgeable, TResp>: NSObject {
    private let tag: String
    
    private let requestName: String
    private let requestPayload: TReq?
    private let responseClass: TResp.Type
    private let responseListener: ElectrodeRequestHandler
    
    init(requestName: String, requestPayload: TReq?, respClass: TResp.Type, responseListener: ElectrodeRequestHandler) {
        self.tag = String(describing: type(of: self))
        
        self.requestName = requestName
        self.requestPayload = requestPayload
        self.responseClass = respClass
        self.responseListener = responseListener
    }
    
    func execute() {
        print("RequestProcessor started processing request (\(requestName)) with payload (\(requestPayload))")
        
        // Convert the payload
        let requestDictionary = requestPayload?.toDictionary()
        
        // Build a new bridge request to send out
        let request = ElectrodeBridgeRequest(name: requestName, data: requestDictionary as! [AnyHashable : Any]?, mode: EBDispatchMode.JS)
        
        let listener = ElectrodeRequestCompletionListenerImplementor<TReq>()
        
        ElectrodeBridgeHolder.sharedInstance().send(request, completionListener: listener)
    }
}

class ElectrodeRequestCompletionListenerImplementor<T: Bridgeable>: NSObject, ElectrodeRequestCompletionListener {
    
    private var executor: ((_ listener: Bridgeable?) -> ())? = nil
    
    func onSuccess(_ data: [AnyHashable : Any]?) {
        
        print("Processing final result for the request with payload bundle (\(data))")
        let result = T.generateObject(data) as? Bridgeable
        
        if let executor = executor {
            executor(result)
        }
    }
    
    func onError(_ code: String, message: String) {
        
    }
}

/*
public class RequestProcessor<TReq, TResp> {
    private final String TAG = RequestProcessor.class.getSimpleName();
    
    private final String requestName;
    private final TReq requestPayload;
    private final Class<TResp> responseClass;
    private final ElectrodeBridgeResponseListener<TResp> responseListener;
    
    public RequestProcessor(@NonNull String requestName, @Nullable TReq requestPayload, @NonNull Class<TResp> respClass, @NonNull ElectrodeBridgeResponseListener<TResp> responseListener) {
        this.requestName = requestName;
        this.requestPayload = requestPayload;
        this.responseClass = respClass;
        this.responseListener = responseListener;
    }
    
    
    public void execute() {
        Logger.d(TAG, "Request processor started processing request(%s)", requestName);
        Bundle data = BridgeArguments.generateBundle(requestPayload, BridgeArguments.Type.REQUEST);
        
        ElectrodeBridgeRequest req = new ElectrodeBridgeRequest.Builder(requestName)
        .withData(data)
        .build();
    
        ElectrodeBridgeHolder.sendRequest(req, new ElectrodeBridgeResponseListener<Bundle>() {
    
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                responseListener.onFailure(failureMessage);
            }
    
            @Override
            public void onSuccess(@Nullable Bundle responseData) {
                TResp response = BridgeArguments.generateObject(responseData, responseClass, BridgeArguments.Type.RESPONSE);
                Logger.d(TAG, "Request processor received the final response(%s) for request(%s)", response, requestName);
                responseListener.onSuccess(response);
            }
        });
    
    }
}*/
