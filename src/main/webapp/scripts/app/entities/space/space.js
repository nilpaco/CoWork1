'use strict';

angular.module('project1App')
    .config(function ($stateProvider) {
        $stateProvider
            .state('space', {
                parent: 'entity',
                url: '/spaces',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'project1App.space.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/space/spaces.html',
                        controller: 'SpaceController as vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('space');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('space.detail', {
                parent: 'entity',
                url: '/space/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'project1App.space.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/space/space-detail.html',
                        controller: 'SpaceDetailController as vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('space');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Space', function($stateParams, Space) {
                        return Space.get({id : $stateParams.id});
                    }],
                    imageSpace: ['$stateParams', 'Image', function ($stateParams, Image) {
                        return Image.imagesByCurrentSpace({id: $stateParams.id});
                    }]
                }
            })
            .state('space.new', {
                parent: 'space',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'project1App.space.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/space/space-dialog.html',
                        controller: 'SpaceDialogController as vm'
                    }
                },
                resolve: {
                    entity: function () {
                        return {
                            name: null,
                            description: null,
                            price: null,
                            personMax: null,
                            streetAddress: null,
                            lat: null,
                            lng: null,
                            id: null
                        };
                    },
                    imageentity: function (){
                        return{
                            image: null,
                            space_id: null
                        }
                    }
                }
            })
            .state('space.edit', {
                parent: 'space',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/space/space-dialog.html',
                        controller: 'SpaceDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Space', function(Space) {
                                return Space.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('space', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('space.delete', {
                parent: 'space',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/space/space-delete-dialog.html',
                        controller: 'SpaceDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Space', function(Space) {
                                return Space.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('space', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
        .state('review.edit', {
            parent: 'space',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER'],
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'scripts/app/entities/review/review-dialog.html',
                    controller: 'ReviewDialogController',
                    size: 'lg',
                    resolve: {
                        entity: ['Review', function(Review) {
                            return Review.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function(result) {
                    $state.go('review', null, { reload: true });
                }, function() {
                    $state.go('^');
                })
            }]
        })
            .state('review.delete', {
                parent: 'space',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/review/review-delete-dialog.html',
                        controller: 'ReviewDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Review', function(Review) {
                                return Review.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('review', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });

    });
