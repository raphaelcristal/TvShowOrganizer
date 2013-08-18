'use strict';
angular
    .module('tvshoworganizer', [])
    .config(function($routeProvider) {
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
                controller: 'DashboardCtrl'
            })
            .when('/organize', {
                templateUrl: 'assets/partials/organize.html',
                controller: 'OrganizeCtrl'
            })
            .when('/shows', {
                templateUrl: 'assets/partials/shows.html',
                controller: 'ShowsCtrl'
            })
            .when('/shows/:id', {
                templateUrl: 'assets/partials/showDetails.html',
                controller: 'ShowDetailsCtrl',
                resolve: {
                    showRequest: function($http, $route) {
                        return $http.get('/shows/' + $route.current.params.id);
                    }
                }
            })
            .when('/actors/:id', {
                templateUrl: 'assets/partials/actor.html',
                controller: 'ActorCtrl',
                resolve: {
                    actorRequest: function($http, $route) {
                        return $http.get('/actors/' + $route.current.params.id + '/shows');
                    }
                }
            })
            .when('/networks/:id', {
                templateUrl: 'assets/partials/network.html',
                controller: 'NetworkCtrl',
                resolve: {
                    networkRequest: function($http, $route) {
                        return $http.get('/networks/' + $route.current.params.id + '/shows');
                    }
                }
            })
            .when('/profile', {
                templateUrl: 'assets/partials/profile.html',
                controller: 'ProfileCtrl'
            })
            .otherwise({
                redirectTo: '/'
            })
    })
    .config(function($httpProvider) {

        var logoutOnAccessForbidden = function($location, $q, User, FlashMessage) {

            var success = function(response) {
                if(!User.isLoggedIn() && $location.path() !== '/' && $location.path() !== '/api') {
                    $location.path('/');
                    return $q.reject(response);
                }
                if(response.data.error) {
                    FlashMessage.setErrorMessage(response.data.error || 'Something went wrong.');
                } else {
                    FlashMessage.clearErrorMessage();
                }
                return response;
            };

            var error = function(response) {
                if(response.status === 401) {
                    User.logOut();
                    $location.path('/');
                }
                FlashMessage.setErrorMessage(response.data.error);
                return $q.reject(response);
            };

            return function(promise) {
                return promise.then(success, error);
            };
        };

        return $httpProvider.responseInterceptors.push(logoutOnAccessForbidden);
    })
    .filter('fromTimestamp', function() {

        return function(timestamp) {

            if(!timestamp) {
                return '';
            }
            var date = new Date(timestamp);
            return date.toDateString();

        };
    })
    .factory('FlashMessage', function() {

        var errorMessage = '';
        var successMessage = '';

        return {
            setErrorMessage: function(message) {
                errorMessage = message;
            },
            getErrorMessage: function() {
                return errorMessage;
            },
            clearErrorMessage: function() {
                errorMessage = '';
            },
            setSuccessMessage: function(message) {
                successMessage = message;
            },
            getSuccessMessage: function() {
                return successMessage;
            },
            clearSuccessMessage: function() {
                successMessage = '';
            }
        }

    })
    .factory('User', function(){

        var storageUser = JSON.parse(localStorage.getItem("user"));

        var save = function() {
            localStorage.setItem("user", JSON.stringify(storageUser));
        };

        return {
            isLoggedIn: function() {
                return !!storageUser;
            },
            logIn: function(user) {
                storageUser = user;
                save();
            },
            logOut: function() {
                localStorage.removeItem("user");
                storageUser = null;
            },
            getUserId: function() {
                return storageUser.id;
            },
            getShows: function() {
                return storageUser ? storageUser.shows : [];
            },
            getUser: function() {
                return storageUser;
            },
            isSubscribed: function(showId) {
                return !! _.find(storageUser.shows, {id: showId});
            },
            addShow: function(show) {
                storageUser.shows.push(show);
                save();
            },
            removeShow: function(showId) {
                storageUser.shows = _.reject(storageUser.shows, function(show) {
                    return show.id === showId;
                });
                save();
            }
        };
    })
    .factory('LatestEpisodes', function($http, User) {

        var latestEpisodes = [];

        var load = function() {
            $http
                .get('/users/' + User.getUserId() + '/latestEpisodes')
                .success(function(data) {
                    if(!data.error) {
                        data.sort(function(a, b) {
                            return a.airtime - b.airtime;
                        });
                        latestEpisodes = data;
                    }

                })
        };
        load();

        return {
            getEpisodes: function( ) {
                return latestEpisodes;
            },
            refresh: load
        };
    })
    .controller('SignUpCtrl', function($scope, $http, $location, User) {

        $scope.isLoggedIn = User.isLoggedIn;

        $scope.error = '';

        $scope.signUp = function(email, password) {

            $http({
                url: '/users',
                method: 'POST',
                params: {email: email, password: password}
            })
            .success(function(data) {
                if(!data.error) {
                    User.logIn(data);
                    $location.path('/dashboard');
                }
             })
        };
    })
    .controller('TopbarCtrl', function($scope, $location, $http, User, FlashMessage) {

        $scope.isLoggedIn = User.isLoggedIn;
        $scope.flashErrorMessage = FlashMessage.getErrorMessage;
        $scope.flashSuccessMessage = FlashMessage.getSuccessMessage;

        $scope.logIn = function(email, password) {

            $http({
                url: '/users/authtoken',
                method: 'POST',
                params: {
                    email: email,
                    password: password
                }

            }).success(function(data) {
                if(!data.error) {
                    User.logIn(data);
                    $location.path('/dashboard');
                }
            })
        };

        $scope.logOut = function() {

            $location.path('/');
            User.logOut();

        };

        $scope.selectedClass = function(partial) {

            var currentPartial = $location.path().substring(1);
            return partial === currentPartial ? 'pure-menu-selected' : '';

        };
    })
    .controller('DashboardCtrl', function($scope, LatestEpisodes, User) {

        $scope.latestEpisodes = LatestEpisodes.getEpisodes;

        $scope.userShows = User.getShows;

    })
    .controller('OrganizeCtrl', function($scope, $location, $http, User, LatestEpisodes, FlashMessage) {

        $scope.shows = User.getShows;

        $scope.showDetails = function(id) {
            $location.path('/shows/' + id);
        };

        $scope.unSubscribe = function(showId) {
            $http({
                url: '/users/' + User.getUserId() + '/shows/' + showId,
                method: 'DELETE'
            }).success(function(data) {
                if(!data.error) {
                    User.removeShow(showId);
                    LatestEpisodes.refresh();
                    FlashMessage.setSuccessMessage('Successfully unsubscribed from "' + data.title + '".');
                }
            })
        };
    })
    .controller('ShowsCtrl', function($scope, $location, $http, User, LatestEpisodes, FlashMessage) {

        $scope.searchedShows = [];
        $scope.info = '';

        $scope.searchShowsByName = function(query) {

            $http
                .get('/shows/search', {params: {title: query}})
                .success(function(shows) {
                    if(shows.length > 0) {
                        $scope.searchedShows = shows;
                        $scope.info = '';
                    } else {
                        $scope.searchedShows = [];
                        $scope.info = 'No shows found for "' + query + '".';;
                    }
                 })
        };

        $scope.subscribe = function(id) {
            $http({
                url: '/users/' + User.getUserId() + '/shows/' + id,
                method: 'PUT'
            }).success(function(data) {
                if(!data.error) {
                    User.addShow(data);
                    LatestEpisodes.refresh();
                    FlashMessage.setSuccessMessage('Successfully subscribed to "' + data.title + '".');
                }
            })
        };

        $scope.isSubscribed = User.isSubscribed;

        $scope.showDetails = function(id) {
            $location.path('/shows/' + id);
        };
    })
    .controller('ShowDetailsCtrl', function($scope , $http , $location , User , LatestEpisodes , showRequest, FlashMessage) {

        $scope.show = showRequest.data;

        $scope.visible = _.map($scope.show.seasons, function() { return false });

        $scope.toggle = function(index) {

            _.each($scope.visible, function(element,listIndex) {

                if(index === listIndex) {
                    $scope.visible[index] = !$scope.visible[index];
                } else {
                    $scope.visible[listIndex] = false;
                }

            });

        };

        $scope.subscribe = function(id) {
            $http({
                url: '/users/' + User.getUserId() + '/shows/' + id,
                method: 'PUT'
            }).success(function(data) {
                if(!data.error) {
                    User.addShow(data);
                    LatestEpisodes.refresh();
                    FlashMessage.setSuccessMessage('Successfully subscribed to "' + data.title + '".');
                }
            })
        };

        $scope.unSubscribe = function(showId) {
            $http({
                url: '/users/' + User.getUserId() + '/shows/' + showId,
                method: 'DELETE'
            }).success(function(data) {
                if(!data.error) {
                    User.removeShow(showId);
                    LatestEpisodes.refresh();
                    FlashMessage.setSuccessMessage('Successfully unsubscribed from "' + data.title + '".');
                }
            })
        };

        $scope.isSubscribed = User.isSubscribed;
    })
    .controller('ActorCtrl', function($scope, $http , $location, $route, actorRequest) {

        $scope.shows = actorRequest.data;

        var url = '/actor/' + $route.current.params.id;
        $http.get(url).success( function(actor) {
            if(!actor.error) {
                $scope.actorName = actor.name;
            }
        });

        $scope.showDetails = function(id) {
            $location.path('/shows/' + id);
        };
    })
    .controller('NetworkCtrl', function($scope, $http ,$location, $route, networkRequest) {

        $scope.shows = networkRequest.data;

        var url = '/network/' + $route.current.params.id;
        $http.get(url).success(function(network) {
            if(!network.error) {
                $scope.networkName = network.name;
            }
        });

        $scope.showDetails = function(id) {
            $location.path('/shows/' + id);
        };
    })
    .controller('ProfileCtrl', function($scope, $http, User, FlashMessage) {

        $scope.user = User.getUser();

        $scope.updateProfile = function(oldPassword, newPassword, newPasswordRepeated) {

            if(newPassword !== newPasswordRepeated) {
                FlashMessage.setErrorMessage('Your passwords do not match.');
                return;
            }

            $http({
                url: '/users/' + User.getUserId() + '/password',
                method: 'PUT',
                params: {
                    oldPassword: oldPassword,
                    newPassword: newPassword
                }
            }).success(function(data) {
                if(!data.error) {
                    FlashMessage.setSuccessMessage('Your password has been changed.');
                }
            })

        };
    })
    .run(function($rootScope, FlashMessage) {

        $rootScope.$on("$routeChangeStart", function(event, next, current) {
            FlashMessage.clearSuccessMessage();
        });

    })