'use strict';
angular
    .module('tvshoworganizer', [])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'assets/partials/landingpage.html',
                controller: 'SignUpCtrl'
            })
            .when('/api', {
                templateUrl: 'assets/partials/api.html'
            })
            .when('/dashboard', {
                templateUrl: 'assets/partials/dashboard.html',
                controller: 'DashboardCtrl',
                resolve: {
                    latestEpisodesRequest: ['$http', 'UserService', function ($http, UserService) {
                        return $http.get('/users/' + UserService.getUser().id + '/latestEpisodes',
                            {params: {days: UserService.getUser().settings.passedDaysToShow}});
                    }]
                }
            })
            .when('/subscriptions', {
                templateUrl: 'assets/partials/subscriptions.html',
                controller: 'SubscriptionCtrl'
            })
            .when('/shows', {
                templateUrl: 'assets/partials/shows.html',
                controller: 'ShowsCtrl'
            })
            .when('/shows/:id', {
                templateUrl: 'assets/partials/showDetails.html',
                controller: 'ShowDetailsCtrl',
                resolve: {
                    showRequest: ['$http', '$route', function ($http, $route) {
                        return $http.get('/shows/' + $route.current.params.id);
                    }]
                }
            })
            .when('/actors/:id', {
                templateUrl: 'assets/partials/actor.html',
                controller: 'ActorCtrl',
                resolve: {
                    actorRequest: ['$http', '$route', function ($http, $route) {
                        return $http.get('/actors/' + $route.current.params.id + '/shows');
                    }]
                }
            })
            .when('/networks/:id', {
                templateUrl: 'assets/partials/network.html',
                controller: 'NetworkCtrl',
                resolve: {
                    networkRequest: ['$http', '$route', function ($http, $route) {
                        return $http.get('/networks/' + $route.current.params.id + '/shows');
                    }]
                }
            })
            .when('/settings', {
                templateUrl: 'assets/partials/settings.html',
                controller: 'SettingsCtrl'
            })
            .otherwise({
                redirectTo: '/'
            })
    }])
    .config(['$httpProvider', function ($httpProvider) {

        var logoutOnAccessForbidden = ['$location', '$q', 'UserService', 'FlashMessenger',

            function ($location, $q, UserService, FlashMessenger) {

                var success = function (response) {
                    if (!UserService.isLoggedIn() && $location.path() !== '/' && $location.path() !== '/api') {
                        $location.path('/');
                        return $q.reject(response);
                    }
                    if (response.data.error) {
                        FlashMessenger.showErrorMessage(response.data.error || 'Something went wrong.');
                    }
                    return response;
                };

                var error = function (response) {
                    if (response.status === 401) {
                        UserService.logOut();
                        $location.path('/');
                    }
                    FlashMessenger.showErrorMessage(response.data.error || 'Something went wrong.');
                    return $q.reject(response);
                };

                return function (promise) {
                    return promise.then(success, error);
                };
            }];

        return $httpProvider.responseInterceptors.push(logoutOnAccessForbidden);
    }])
    .directive('ngEnter', function () {
        return function (scope, element, attrs) {
            element.bind("keydown keypress", function (event) {
                if (event.which === 13) {
                    scope.$apply(function () {
                        scope.$eval(attrs.ngEnter);
                    });

                    event.preventDefault();
                }
            });
        };
    })
    .directive('ngBlur', function () {
        return function (scope, elem, attrs) {
            elem.bind('blur', function () {
                scope.$apply(attrs.ngBlur);
            });
        };
    })
    .filter('fromTimestamp', function () {

        return function (timestamp) {

            if (!timestamp) {
                return '';
            }
            var date = new Date(timestamp);
            return date.toDateString();

        };
    })
    .filter('leadingZero', function () {

        return function (number) {

            if (number === 0) return number;
            if (number < 10) return '0' + number;
            return number;

        };
    })
    .factory('FlashMessenger', function () {

        return {
            showErrorMessage: function (message) {
                $.bootstrapGrowl(message, {
                    offset: {from: 'top', amount: 55},
                    type: 'danger'
                });
            },
            showSuccessMessage: function (message) {
                $.bootstrapGrowl(message, {
                    offset: {from: 'top', amount: 55},
                    type: 'success'
                });
            }
        }

    })
    .factory('UserService', function () {

        var user = JSON.parse(document.querySelector('#user').textContent);

        return {
            getUser: function () {
                return user;
            },
            isLoggedIn: function () {
                return !!user;
            },
            logIn: function (userData) {
                user = userData;
            },
            logOut: function () {
                user = null;
            },
            getShows: function () {
                return user ? user.shows : [];
            },
            isSubscribed: function (showId) {
                return user.shows.filter(function (show) {
                    return show.id === showId;
                }).length > 0;
            },
            addShow: function (show) {
                user.shows.push(show);
            },
            removeShow: function (showId) {
                user.shows = user.shows.filter(function (show) {
                    return show.id !== showId;
                });
            },
            updateHideDescriptionSetting: function (hide) {
                user.settings.hideDescriptions = hide;
            },
            updatePassedDaysToShow: function (passedDaysToShow) {
                user.settings.passedDaysToShow = passedDaysToShow;
            }
        };
    })
    .controller('SignUpCtrl', ['$scope', '$http', '$location', 'UserService',

        function ($scope, $http, $location, UserService) {

            $scope.isLoggedIn = UserService.isLoggedIn;

            $scope.error = '';

            $scope.signUp = function (email, password) {

                $http({
                    url: '/users',
                    method: 'POST',
                    params: {email: email, password: password}
                })
                    .success(function (data) {
                        if (!data.error) {
                            UserService.logIn(data);
                            $location.path('/dashboard');
                        }
                    })
            };
        }])
    .controller('TopbarCtrl', ['$scope', '$location', '$http', 'UserService',

        function ($scope, $location, $http, UserService) {

            $scope.isLoggedIn = UserService.isLoggedIn;

            $scope.logIn = function (email, password) {

                $http({
                    url: '/users/authtoken',
                    method: 'GET',
                    params: {
                        email: email,
                        password: password
                    }

                }).success(function (data) {
                        if (!data.error) {
                            UserService.logIn(data);
                            $location.path('/dashboard');
                        }
                    })
            };

            $scope.logOut = function () {

                $http.get('/users/logOut').success(function () {
                    $location.path('/');
                    UserService.logOut();
                });

            };

            $scope.selectedClass = function (partial) {

                var currentPartial = $location.path().substring(1);
                return partial === currentPartial ? 'active' : '';

            };
        }])
    .controller('DashboardCtrl', ['$scope', 'UserService', 'latestEpisodesRequest',

        function ($scope, UserService, latestEpisodesRequest) {

            $scope.hasRun = function (episode) {
                return new Date(episode.airtime) < new Date();
            }

            $scope.latestEpisodes = latestEpisodesRequest.data;

            $scope.userShows = UserService.getShows;

            $scope.settings = UserService.getUser().settings;

        }])
    .controller('SubscriptionCtrl', ['$scope', '$location', '$http', 'UserService',

        function ($scope, $location, $http, UserService) {

            $scope.shows = UserService.getShows;

            $scope.showDetails = function (id) {
                $location.path('/shows/' + id);
            };

            $scope.unSubscribe = function (showId) {
                $http({
                    url: '/users/' + UserService.getUser().id + '/shows/' + showId,
                    method: 'DELETE'
                }).success(function (data) {
                        if (!data.error) {
                            UserService.removeShow(showId);
                        }
                    })
            };
        }])
    .controller('ShowsCtrl', ['$scope', '$location', '$http', 'UserService',

        function ($scope, $location, $http, UserService) {

            $scope.searchedShows = [];
            $scope.searchedShowsTvdb = [];
            $scope.info = '';
            $scope.isSearching = false;
            $scope.showTvdbInformation = false;

            $scope.searchShowOnTvdb = function (query) {
                $scope.showTvdbInformation = false;
                $http
                    .get('/shows/search/tvdb', {params: {title: query}})
                    .success(function (shows) {
                        $scope.isSearching = false;
                        if (shows.length > 0) {
                            $scope.searchedShowsTvdb = shows;
                        } else {
                            $scope.searchedShowsTvdb = [];
                            $scope.info = 'Sorry we couldn\' find anything for your search.';
                        }
                    });
            };

            $scope.searchShowsByName = function (query) {
                $scope.isSearching = true;
                $http
                    .get('/shows/search', {params: {title: query}})
                    .success(function (shows) {
                        if (shows.length > 0) {
                            $scope.isSearching = false;
                            $scope.showTvdbInformation = true;
                            $scope.searchedShowsTvdb = [];
                            $scope.searchedShows = shows;
                            $scope.info = '';
                        } else {
                            $scope.searchedShows = [];
                            $scope.searchShowOnTvdb(query);
                        }
                    });
            };

            $scope.importShow = function (show) {
                show.isSubscribing = true;
                $http({
                    url: '/shows',
                    method: 'POST',
                    params: {tvdbId: show.seriesid}
                }).success(function (data) {
                        show.isSubscribing = false;
                        if (!data.error) {
                            show.isSubscribed = true;
                            $scope.subscribe(data.id);
                        }
                    })
            }

            $scope.subscribe = function (id) {
                $http({
                    url: '/users/' + UserService.getUser().id + '/shows/' + id,
                    method: 'PUT'
                }).success(function (data) {
                        if (!data.error) {
                            UserService.addShow(data);
                        }
                    })
            };

            $scope.isSubscribed = UserService.isSubscribed;

            $scope.showDetails = function (id) {
                $location.path('/shows/' + id);
            };
        }])
    .controller('ShowDetailsCtrl', ['$scope', '$http', '$location', 'UserService', 'showRequest',

        function ($scope, $http, $location, UserService, showRequest) {

            $scope.show = showRequest.data;

            $scope.subscribe = function (id) {
                $http({
                    url: '/users/' + UserService.getUser().id + '/shows/' + id,
                    method: 'PUT'
                }).success(function (data) {
                        if (!data.error) {
                            UserService.addShow(data);
                        }
                    })
            };

            $scope.unSubscribe = function (showId) {
                $http({
                    url: '/users/' + UserService.getUser().id + '/shows/' + showId,
                    method: 'DELETE'
                }).success(function (data) {
                        if (!data.error) {
                            UserService.removeShow(showId);
                        }
                    })
            };

            $scope.isSubscribed = UserService.isSubscribed;
        }])
    .controller('ActorCtrl', ['$scope', '$http', '$location', '$route', 'actorRequest',

        function ($scope, $http, $location, $route, actorRequest) {

            $scope.shows = actorRequest.data;

            var url = '/actor/' + $route.current.params.id;
            $http.get(url).success(function (actor) {
                if (!actor.error) {
                    $scope.actorName = actor.name;
                }
            });

            $scope.showDetails = function (id) {
                $location.path('/shows/' + id);
            };
        }])
    .controller('NetworkCtrl', ['$scope', '$http', '$location', '$route', 'networkRequest',

        function ($scope, $http, $location, $route, networkRequest) {

            $scope.shows = networkRequest.data;

            var url = '/network/' + $route.current.params.id;
            $http.get(url).success(function (network) {
                if (!network.error) {
                    $scope.networkName = network.name;
                }
            });

            $scope.showDetails = function (id) {
                $location.path('/shows/' + id);
            };
        }])
    .controller('SettingsCtrl', ['$scope', '$http', 'UserService', 'FlashMessenger',

        function ($scope, $http, UserService, FlashMessenger) {

            $scope.user = UserService.getUser();

            $scope.updateHideDescriptionsSetting = function (hideDescriptions) {
                $http({
                    url: '/users/' + UserService.getUser().id + '/settings/hideDescriptions',
                    method: 'PUT',
                    params: {
                        hideShowDescriptions: hideDescriptions
                    }
                }).success(function (data) {
                        if (!data.error) {
                            UserService.updateHideDescriptionSetting(hideDescriptions);
                            FlashMessenger.showSuccessMessage('Your settings have been updated.');
                        }
                    })
            };

            $scope.updatePassedDaysToShow = function (passedDaysToShow) {
                $http({
                    url: '/users/' + UserService.getUser().id + '/settings/passedDaysToShow',
                    method: 'PUT',
                    params: {
                        days: passedDaysToShow
                    }
                }).success(function (data) {
                        if (!data.error) {
                            UserService.updatePassedDaysToShow(passedDaysToShow);
                            FlashMessenger.showSuccessMessage('Your settings have been updated.');
                        }
                    })
            };

            $scope.updateProfile = function (oldPassword, newPassword, newPasswordRepeated) {

                if (newPassword !== newPasswordRepeated) {
                    FlashMessenger.showErrorMessage('Your passwords do not match.');
                    return;
                }

                $http({
                    url: '/users/' + UserService.getUser().id + '/password',
                    method: 'PUT',
                    params: {
                        oldPassword: oldPassword,
                        newPassword: newPassword
                    }
                }).success(function (data) {
                        if (!data.error) {
                            FlashMessenger.showSuccessMessage('Your password has been changed.');
                        }
                    })

            };
        }])