'use strict';
angular
    .module('tvshoworganizer', [])
    .config(function ($routeProvider) {
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
                    latestEpisodesRequest: function ($http, User) {
                        return $http.get('/users/' + User.getUserId() + '/latestEpisodes',
                            {params: {days: User.getUser().settings.passedDaysToShow}});
                    }
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
                    showRequest: function ($http, $route) {
                        return $http.get('/shows/' + $route.current.params.id);
                    }
                }
            })
            .when('/actors/:id', {
                templateUrl: 'assets/partials/actor.html',
                controller: 'ActorCtrl',
                resolve: {
                    actorRequest: function ($http, $route) {
                        return $http.get('/actors/' + $route.current.params.id + '/shows');
                    }
                }
            })
            .when('/networks/:id', {
                templateUrl: 'assets/partials/network.html',
                controller: 'NetworkCtrl',
                resolve: {
                    networkRequest: function ($http, $route) {
                        return $http.get('/networks/' + $route.current.params.id + '/shows');
                    }
                }
            })
            .when('/settings', {
                templateUrl: 'assets/partials/settings.html',
                controller: 'SettingsCtrl'
            })
            .otherwise({
                redirectTo: '/'
            })
    })
    .config(function ($httpProvider) {

        var logoutOnAccessForbidden = function ($location, $q, User, FlashMessenger) {

            var success = function (response) {
                if (!User.isLoggedIn() && $location.path() !== '/' && $location.path() !== '/api') {
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
                    User.logOut();
                    $location.path('/');
                }
                FlashMessenger.showErrorMessage(response.data.error || 'Something went wrong.');
                return $q.reject(response);
            };

            return function (promise) {
                return promise.then(success, error);
            };
        };

        return $httpProvider.responseInterceptors.push(logoutOnAccessForbidden);
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
    .factory('User', function () {

        var storageUser = JSON.parse(localStorage.getItem("user"));

        var save = function () {
            localStorage.setItem("user", JSON.stringify(storageUser));
        };

        return {
            isLoggedIn: function () {
                return !!storageUser;
            },
            logIn: function (user) {
                storageUser = user;
                save();
            },
            logOut: function () {
                localStorage.removeItem("user");
                storageUser = null;
            },
            getUserId: function () {
                return storageUser.id;
            },
            getShows: function () {
                return storageUser ? storageUser.shows : [];
            },
            getUser: function () {
                return storageUser;
            },
            isSubscribed: function (showId) {
                return !!_.find(storageUser.shows, {id: showId});
            },
            addShow: function (show) {
                storageUser.shows.push(show);
                save();
            },
            removeShow: function (showId) {
                storageUser.shows = _.reject(storageUser.shows, function (show) {
                    return show.id === showId;
                });
                save();
            },
            updateHideDescriptionSetting: function (hide) {
                storageUser.settings.hideDescriptions = hide;
                save();
            },
            updatePassedDaysToShow: function (passedDaysToShow) {
                storageUser.settings.passedDaysToShow = passedDaysToShow;
                save();
            }
        };
    })
    .controller('SignUpCtrl', function ($scope, $http, $location, User) {

        $scope.isLoggedIn = User.isLoggedIn;

        $scope.error = '';

        $scope.signUp = function (email, password) {

            $http({
                url: '/users',
                method: 'POST',
                params: {email: email, password: password}
            })
                .success(function (data) {
                    if (!data.error) {
                        User.logIn(data);
                        $location.path('/dashboard');
                    }
                })
        };
    })
    .controller('TopbarCtrl', function ($scope, $location, $http, User) {

        $scope.isLoggedIn = User.isLoggedIn;

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
                        User.logIn(data);
                        $location.path('/dashboard');
                    }
                })
        };

        $scope.logOut = function () {

            $location.path('/');
            User.logOut();

        };

        $scope.selectedClass = function (partial) {

            var currentPartial = $location.path().substring(1);
            return partial === currentPartial ? 'active' : '';

        };
    })
    .controller('DashboardCtrl', function ($scope, User, latestEpisodesRequest) {

        $scope.hasRun = function (episode) {
            return new Date(episode.airtime) < new Date();
        }

        $scope.latestEpisodes = latestEpisodesRequest.data;

        $scope.userShows = User.getShows;

        $scope.settings = User.getUser().settings;

    })
    .controller('SubscriptionCtrl', function ($scope, $location, $http, User) {

        $scope.shows = User.getShows;

        $scope.showDetails = function (id) {
            $location.path('/shows/' + id);
        };

        $scope.unSubscribe = function (showId) {
            $http({
                url: '/users/' + User.getUserId() + '/shows/' + showId,
                method: 'DELETE'
            }).success(function (data) {
                    if (!data.error) {
                        User.removeShow(showId);
                    }
                })
        };
    })
    .controller('ShowsCtrl', function ($scope, $location, $http, User) {

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
                url: '/users/' + User.getUserId() + '/shows/' + id,
                method: 'PUT'
            }).success(function (data) {
                    if (!data.error) {
                        User.addShow(data);
                    }
                })
        };

        $scope.isSubscribed = User.isSubscribed;

        $scope.showDetails = function (id) {
            $location.path('/shows/' + id);
        };
    })
    .controller('ShowDetailsCtrl', function ($scope, $http, $location, User, showRequest) {

        $scope.show = showRequest.data;

        $scope.subscribe = function (id) {
            $http({
                url: '/users/' + User.getUserId() + '/shows/' + id,
                method: 'PUT'
            }).success(function (data) {
                    if (!data.error) {
                        User.addShow(data);
                    }
                })
        };

        $scope.unSubscribe = function (showId) {
            $http({
                url: '/users/' + User.getUserId() + '/shows/' + showId,
                method: 'DELETE'
            }).success(function (data) {
                    if (!data.error) {
                        User.removeShow(showId);
                    }
                })
        };

        $scope.isSubscribed = User.isSubscribed;
    })
    .controller('ActorCtrl', function ($scope, $http, $location, $route, actorRequest) {

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
    })
    .controller('NetworkCtrl', function ($scope, $http, $location, $route, networkRequest) {

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
    })
    .controller('SettingsCtrl', function ($scope, $http, User, FlashMessenger) {

        $scope.user = User.getUser();

        $scope.updateHideDescriptionsSetting = function (hideDescriptions) {
            $http({
                url: '/users/' + User.getUserId() + '/settings/hideDescriptions',
                method: 'PUT',
                params: {
                    hideShowDescriptions: hideDescriptions
                }
            }).success(function (data) {
                    if (!data.error) {
                        User.updateHideDescriptionSetting(hideDescriptions);
                        FlashMessenger.showSuccessMessage('Your settings have been updated.');
                    }
                })
        };

        $scope.updatePassedDaysToShow = function (passedDaysToShow) {
            $http({
                url: '/users/' + User.getUserId() + '/settings/passedDaysToShow',
                method: 'PUT',
                params: {
                    days: passedDaysToShow
                }
            }).success(function (data) {
                    if (!data.error) {
                        User.updatePassedDaysToShow(passedDaysToShow);
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
                url: '/users/' + User.getUserId() + '/password',
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
    })