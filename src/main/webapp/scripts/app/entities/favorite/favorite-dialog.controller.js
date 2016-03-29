'use strict';

angular.module('project1App').controller('FavoriteDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Favorite', 'Space', 'User',
        function($scope, $stateParams, $uibModalInstance, entity, Favorite, Space, User) {

        $scope.favorite = entity;
        $scope.spaces = Space.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            Favorite.get({id : id}, function(result) {
                $scope.favorite = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('project1App:favoriteUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.favorite.id != null) {
                Favorite.update($scope.favorite, onSaveSuccess, onSaveError);
            } else {
                Favorite.save($scope.favorite, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
