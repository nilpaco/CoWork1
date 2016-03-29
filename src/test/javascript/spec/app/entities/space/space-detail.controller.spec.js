'use strict';

describe('Controller Tests', function() {

    describe('Space Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSpace, MockService, MockImage, MockFavorite, MockReview, MockConversation, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSpace = jasmine.createSpy('MockSpace');
            MockService = jasmine.createSpy('MockService');
            MockImage = jasmine.createSpy('MockImage');
            MockFavorite = jasmine.createSpy('MockFavorite');
            MockReview = jasmine.createSpy('MockReview');
            MockConversation = jasmine.createSpy('MockConversation');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Space': MockSpace,
                'Service': MockService,
                'Image': MockImage,
                'Favorite': MockFavorite,
                'Review': MockReview,
                'Conversation': MockConversation,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("SpaceDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'project1App:spaceUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
