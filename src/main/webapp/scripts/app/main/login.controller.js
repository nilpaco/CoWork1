/**
 * Created by nilpanescoll on 31/3/16.
 */
'use strict';

angular.module('project1App')
    .controller('LoginController2', function ($scope, Principal) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
        });
