/* alv-ch-ng.text-truncate - 0.2.0 - 2015-03-06 - Copyright (c) 2015 Informatik der Arbeitslosenversicherung; */
;(function () {
    'use strict';

    var module = angular.module('alv-ch-ng.text-truncate', []);

    module.directive( "ngTextTruncate", function( $compile, ValidationServices, CharBasedTruncation, WordBasedTruncation ) {
        return {
            restrict: "A",
            scope: {
                text: "=ngTextTruncate",
                charsThreshould: "@ngTtCharsThreshold",
                wordsThreshould: "@ngTtWordsThreshold",
                customMoreLabel: "@ngTtMoreLabel",
                customLessLabel: "@ngTtLessLabel"
            },
            controller: function( $scope, $attrs ) {
                $scope.toggleShow = function() {
                    $scope.open = !$scope.open;
                };

                $scope.useToggling = $attrs.ngTtNoToggling === undefined;
            },
            link: function( $scope, $element ) {
                $scope.open = false;

                ValidationServices.failIfWrongThreshouldConfig( $scope.charsThreshould, $scope.wordsThreshould );

                var CHARS_THRESHOLD = parseInt( $scope.charsThreshould );
                var WORDS_THRESHOLD = parseInt( $scope.wordsThreshould );

                $scope.$watch( "text", function() {
                    $element.empty();

                    if( CHARS_THRESHOLD ) {
                        if( $scope.text && CharBasedTruncation.truncationApplies( $scope, CHARS_THRESHOLD ) ) {
                            CharBasedTruncation.applyTruncation( CHARS_THRESHOLD, $scope, $element );

                        } else {
                            $element.append( $scope.text );
                        }

                    } else {

                        if( $scope.text && WordBasedTruncation.truncationApplies( $scope, WORDS_THRESHOLD ) ) {
                            WordBasedTruncation.applyTruncation( WORDS_THRESHOLD, $scope, $element );

                        } else {
                            $element.append( $scope.text );
                        }

                    }
                } );
            }
        };
    } );



    module.factory( "ValidationServices", function() {
            return {
                failIfWrongThreshouldConfig: function( firstThreshould, secondThreshould ) {
                    if( (! firstThreshould && ! secondThreshould) || (firstThreshould && secondThreshould) ) {
                        throw "You must specify one, and only one, type of threshould (chars or words)";
                    }
                }
            };
        });



    module.factory( "CharBasedTruncation", function( $compile ) {
            return {
                truncationApplies: function( $scope, threshould ) {
                    return $scope.text.length > threshould;
                },

                applyTruncation: function( threshould, $scope, $element ) {
                    var more =  $scope.customMoreLabel || "<md-button style='color: #ff4081; display: block;'>+ More</md-button>";
                    var less =  $scope.customLessLabel || "<md-button style='color: #ff4081; display: block;'>- Less</md-button>";
                    if( $scope.useToggling ) {
                        var el = angular.element(
                            "<span>" +
                                $scope.text.substr( 0, threshould ) +
                                "<span ng-show='!open'>...</span>" +
                                "<span class='ngTruncateToggleText' ng-click='toggleShow()' ng-show='!open'>"+
                                    more+
                                "</span>" +
                                "<span ng-show='open'>" +
                                    $scope.text.substring( threshould ) +
                                    "<span class='ngTruncateToggleText' ng-click='toggleShow()'>"+
                                        less+
                                    "</span>" +
                                "</span>" +
                            "</span>" );
                        $compile( el )( $scope );
                        $element.append( el );

                    } else {
                        $element.append( $scope.text.substr( 0, threshould ) + "..." );

                    }
                }
            };
        });



    module.factory( "WordBasedTruncation", function( $compile ) {
            return {
                truncationApplies: function( $scope, threshould ) {
                    return $scope.text.split( " " ).length > threshould;
                },

                applyTruncation: function( threshould, $scope, $element ) {
                    var more =  $scope.customMoreLabel || "<span translate='common_text_truncate_more'></span>";
                    var less =  $scope.customLessLabel || "<span translate='common_text_truncate_less'></span>";
                    var splitText = $scope.text.split( " " );
                    if( $scope.useToggling ) {
                        var el = angular.element(
                            "<span>" +
                                splitText.slice( 0, threshould ).join( " " ) +
                                "<span ng-show='!open'>...</span>&nbsp;" +
                                "<span class='btn-link ngTruncateToggleText' ng-click='toggleShow()' ng-show='!open'>"+
                                    more+
                                "</span>" +
                                "<span ng-show='open'>" +
                                    splitText.slice( threshould, splitText.length ).join( " " ) +
                                    "<span class='btn-link ngTruncateToggleText' ng-click='toggleShow()'>"+
                                        less+
                                    "</span>" +
                                "</span>" +
                            "</span>" );
                        $compile( el )( $scope );
                        $element.append( el );

                    } else {
                        $element.append( splitText.slice( 0, threshould ).join( " " ) + "..." );
                    }
                }
            };
        });

}());
