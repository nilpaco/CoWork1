'use strict';

describe('Controller Tests', function() {

    describe('Service Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockService, MockSpace;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockService = jasmine.createSpy('MockService');
            MockSpace = jasmine.createSpy('MockSpace');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Service': MockService,
                'Space': MockSpace
            };
            createController = function() {
                $injector.get('$controller')("ServiceDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'project1App:serviceUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
