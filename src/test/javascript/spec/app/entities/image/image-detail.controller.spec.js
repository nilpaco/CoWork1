'use strict';

describe('Controller Tests', function() {

    describe('Image Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockImage, MockSpace;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockImage = jasmine.createSpy('MockImage');
            MockSpace = jasmine.createSpy('MockSpace');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Image': MockImage,
                'Space': MockSpace
            };
            createController = function() {
                $injector.get('$controller')("ImageDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'project1App:imageUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
