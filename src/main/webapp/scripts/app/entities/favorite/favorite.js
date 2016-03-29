'use strict';

angular.module('project1App')
    .config(function ($stateProvider) {
        $stateProvider
            .state('favorite', {
                parent: 'entity',
                url: '/favorites',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'project1App.favorite.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/favorite/favorites.html',
                        controller: 'FavoriteController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('favorite');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('favorite.detail', {
                parent: 'entity',
                url: '/favorite/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'project1App.favorite.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/favorite/favorite-detail.html',
                        controller: 'FavoriteDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('favorite');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Favorite', function($stateParams, Favorite) {
                        return Favorite.get({id : $stateParams.id});
                    }]
                }
            })
            .state('favorite.new', {
                parent: 'favorite',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/favorite/favorite-dialog.html',
                        controller: 'FavoriteDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    liked: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('favorite', null, { reload: true });
                    }, function() {
                        $state.go('favorite');
                    })
                }]
            })
            .state('favorite.edit', {
                parent: 'favorite',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/favorite/favorite-dialog.html',
                        controller: 'FavoriteDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Favorite', function(Favorite) {
                                return Favorite.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('favorite', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('favorite.delete', {
                parent: 'favorite',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/favorite/favorite-delete-dialog.html',
                        controller: 'FavoriteDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Favorite', function(Favorite) {
                                return Favorite.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('favorite', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
