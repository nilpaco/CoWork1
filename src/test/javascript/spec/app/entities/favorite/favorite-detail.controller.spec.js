'use strict';

describe('Controller Tests', function() {

    describe('Favorite Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockFavorite, MockSpace, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockFavorite = jasmine.createSpy('MockFavorite');
            MockSpace = jasmine.createSpy('MockSpace');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Favorite': MockFavorite,
                'Space': MockSpace,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("FavoriteDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'project1App:favoriteUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
