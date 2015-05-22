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

    $http.get("/bookMarks").success(function(data, status, headers, config) {
	$scope.user.bookMarks = data
	localStorageService.set("chengpohi", $scope.user);
    }).error(function(data, status, headers, config) {
	$scope.user = getItem("chengpohi")
    });

    $scope.append = function (user, bookMarkName, bookMarkUrl) {
	user.bookMarks.push({'url': bookMarkUrl, 'name': bookMarkName});
	postBookMark(bookMarkName, bookMarkUrl);

	$scope.bookMarkName = '';
	$scope.bookMarkUrl = '';
    };
    
    $scope.removeMarkItem = function(user, index) {
	var bookMarkId = user.bookMarks[index].id;
	user.bookMarks.splice(index, 1);
	deleteBookMark(bookMarkId);
    }

    function getItem(key) {
	return localStorageService.get(key);
    }

    function postBookMark(bookMarkName, bookMarkUrl) {
	$http.post('/bookMarks', {'name': bookMarkName, 'url': bookMarkUrl}).
	    success(function(data, status, headers, config) {
	    }).
	    error(function(data, status, headers, config) {
	    });
    }
    
    function deleteBookMark(bookMarkId) {
	$http.delete('/bookMarks/' + bookMarkId).
	    success(function(data, status, headers, config) {
	    }).
	    error(function(data, status, headers, config) {
	    });
    }
});
