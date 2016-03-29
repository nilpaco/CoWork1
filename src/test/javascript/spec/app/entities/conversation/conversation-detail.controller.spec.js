'use strict';

describe('Controller Tests', function() {

    describe('Conversation Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockConversation, MockMessage, MockSpace, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockConversation = jasmine.createSpy('MockConversation');
            MockMessage = jasmine.createSpy('MockMessage');
            MockSpace = jasmine.createSpy('MockSpace');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Conversation': MockConversation,
                'Message': MockMessage,
                'Space': MockSpace,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("ConversationDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'project1App:conversationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
