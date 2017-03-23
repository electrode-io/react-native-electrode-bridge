//
//  EventListenerProcessor.swift
//  ElectrodeReactNativeBridge
//
//  Created by Cody Garvin on 3/2/17.
//  Copyright © 2017 Bharath Marulasiddappa. All rights reserved.
//

import UIKit

class EventListenerProcessor<T: Bridgeable>: NSObject {
    private let tag: String
    private let eventName: String
    private let eventPayloadClass: T.Type
    private let eventListener: ElectrodeEventListener
    
    init(eventName: String, eventPayloadClass: T.Type, eventListener: ElectrodeEventListener) {
        tag = String(describing: type(of: self))
        self.eventName = eventName
        self.eventPayloadClass = eventPayloadClass
        self.eventListener = eventListener
    }
    
    func execute() {
        
        // Build an intermediate eventlistener
        let listener = ElectrodeEventListenerImplementor(eventPayloadClass: self.eventPayloadClass) {
            [weak self] result in
            self?.eventListener.onEvent(result)
        }
        
        // Add the event listener to the bridge
        ElectrodeBridgeHolder.sharedInstance().registerEventListener(eventName, eventListener: listener)
    }
}

class ElectrodeEventListenerImplementor<T: Bridgeable>: NSObject, ElectrodeEventListener {
    
    private var executor: ((_ listener: Bridgeable?) -> ())? = nil
    private let eventPayloadClass: T.Type
    
    init(eventPayloadClass: T.Type, executor: ((_ listener: Bridgeable?) -> ())?) {
        self.eventPayloadClass = eventPayloadClass
        self.executor = executor
    }

    func onEvent(_ eventPayload: Any?) {
        
        print("Processing final result for the event with payload bundle (\(eventPayload))")
        let result = T.generateObject(eventPayload) as? Bridgeable
        
        if let executor = executor {
            executor(result)
        }
    }
}

/*
public class EventListenerProcessor<T> {
    private static final String TAG = EventListenerProcessor.class.getSimpleName();
    
    private final String eventName;
    private final Class<T> eventPayLoadClass;
    private final ElectrodeBridgeEventListener<T> eventListener;
    
    public EventListenerProcessor(@NonNull String eventName, @NonNull final Class<T> eventPayLoadClass, @NonNull final ElectrodeBridgeEventListener<T> eventListener) {
        this.eventName = eventName;
        this.eventPayLoadClass = eventPayLoadClass;
        this.eventListener = eventListener;
    }
    
    public void execute() {
        ElectrodeBridgeEventListener<Bundle> intermediateEventListener = new ElectrodeBridgeEventListener<Bundle>() {
            @Override
            public void onEvent(@Nullable Bundle eventPayload) {
                Logger.d(TAG, "Processing final result for the event with payload bundle(%s)", eventPayload);
                T result = BridgeArguments.generateObject(eventPayload, eventPayLoadClass, BridgeArguments.Type.EVENT);
                eventListener.onEvent(result);
            }
        };
        ElectrodeBridgeHolder.addEventListener(eventName, intermediateEventListener);
    }
}
*/
