var siny = angular.module('siny', ['LocalStorageModule']);

siny.config(function (localStorageServiceProvider) {
    localStorageServiceProvider
	.setPrefix('coolmarks')
	.setStorageType('sessionStorage')
	.setNotify(true, true)
});

siny.controller('sinyCtrl', function($scope, $http, localStorageService) {
    var bookMarkName = $scope.bookMarkName;
    var bookMarkUrl = $scope.bookMarkUrl;
    
    $scope.user = { bookMarks: [] };

    $http.get("/user/chengpohi").success(function(data, status, headers, config) {
	$scope.user.bookMarks = data
	localStorageService.set("chengpohi", $scope.user);
    }).error(function(data, status, headers, config) {
	$scope.user = getItem("chengpohi")
    });

    $scope.append = function (user, bookMarkName, bookMarkUrl) {
	user.bookMarks.push({'url': bookMarkUrl, 'name': bookMarkName});
	$scope.bookMarkName = '';
	$scope.bookMarkUrl = '';
	localStorageService.set("chengpohi", user);
    };
    
    $scope.removeMarkItem = function(user, index) {
	user.bookMarks.splice(index, 1);
	localStorageService.set("chengpohi", user);
    }
    function getItem(key) {
	return localStorageService.get(key);
    }
});
