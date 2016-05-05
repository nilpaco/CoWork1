'use strict';

angular.module('project1App')
    .config(function ($stateProvider) {
        $stateProvider
            .state('image', {
                parent: 'entity',
                url: '/images',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'project1App.image.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/image/images.html',
                        controller: 'ImageController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('image');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('image.detail', {
                parent: 'entity',
                url: '/image/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'project1App.image.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/image/image-detail.html',
                        controller: 'ImageDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('image');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Image', function($stateParams, Image) {
                        return Image.get({id : $stateParams.id});
                    }]
                }
            })
            .state('image.new', {
                parent: 'image',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/image/image-dialog.html',
                        controller: 'ImageDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    image: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('image', null, { reload: true });
                    }, function() {
                        $state.go('image');
                    })
                }]
            })
            .state('image.edit', {
                parent: 'image',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/image/image-dialog.html',
                        controller: 'ImageDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Image', function(Image) {
                                return Image.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('image', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('image.delete', {
                parent: 'image',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/image/image-delete-dialog.html',
                        controller: 'ImageDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Image', function(Image) {
                                return Image.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('image', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
