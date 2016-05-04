;(function () {

  'use strict';

  /**
   * Initially required dependencies.
   * @type {string[]}
   */
  var dependencies = [
    'ngRoute',
    'ngCookies',
    'ngResource',
    'ngAria',
    'pascalprecht.translate',
    'alv-ch-ng.text-truncate'
  ];

  /**
   * The example application.
   */
  var app = angular.module('example-app', dependencies);

  /**
   * Common constants
   */
  app.constant('supportedLanguages', ['de', 'en']);

  /**
   * xSite request & routing definitions
   */
  app.config(function ($routeProvider, $httpProvider) {
      /** Enable cross domain communication **/
      $httpProvider.defaults.headers.useXDomain = true;
      $httpProvider.defaults.withCredentials = true;
      delete $httpProvider.defaults.headers.common['X-Requested-With'];

      /** -- Routings -- **/
      var routes = [
        {path: '/', redirectTo: '/textTruncate'},
        {path: '/textTruncate', templateUrl: '/example/pages/textTruncate.html', controller: 'ExampleCtrl'},
        {path: '/demo2', templateUrl: '/example/pages/demo2.html', controller: 'ExampleCtrl'}
      ];

      for (var i = 0; i < routes.length; i++) {
        var route = routes[i];

        if (route.redirectTo) {
          $routeProvider.when(route.path, {redirectTo: route.redirectTo});
        } else if (route.controller) {
          $routeProvider.when(route.path, {
            templateUrl: route.templateUrl,
            controller: route.controller
          });
        } else {
          $routeProvider.when(route.path, {
            templateUrl: route.templateUrl
          });
        }
      }
    }
  );

  /**
   * angular translate
   */
  app.config(function ($translateProvider, supportedLanguages) {
    $translateProvider.registerAvailableLanguageKeys(supportedLanguages, {
      'en_US': 'en',
      'en_UK': 'en',
      'de_DE': 'de',
      'de_CH': 'de'
    });

    $translateProvider.useStaticFilesLoader({
      prefix: 'locales/messages_',
      suffix: '.json'
    });

    $translateProvider.determinePreferredLanguage();

    $translateProvider.useLocalStorage();
  });

  app.controller('ExampleCtrl', function ($scope) {
      $scope.longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla semper augue vel scelerisque egestas. Praesent odio lacus, porta vitae nisl a, semper tempor elit. Etiam fringilla ut nisl non dictum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus quis eros euismod, elementum tortor ut, sagittis felis. Nulla lectus ante, eleifend non felis pharetra, porta aliquet urna. Curabitur nec elit sit amet tortor accumsan volutpat sed vitae ante. Cras semper consequat nunc, in tincidunt dolor scelerisque eget. Morbi volutpat quis est bibendum aliquet. Sed euismod neque nisl, congue fermentum eros sagittis sit amet. Nulla at tincidunt nibh.";
  });
}());
