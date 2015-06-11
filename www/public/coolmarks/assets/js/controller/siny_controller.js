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
    
    $scope.user = { bookmark: [] };

    $http.get("/bookmark").success(function(data, status, headers, config) {
	$scope.user.bookmark = data
	localStorageService.set("chengpohi", $scope.user);
    }).error(function(data, status, headers, config) {
	$scope.user = getItem("chengpohi")
    });

    $scope.append = function (user, bookMarkName, bookMarkUrl) {
	user.bookmark.push({'url': bookMarkUrl, 'name': bookMarkName});
	postBookMark(bookMarkName, bookMarkUrl);

	$scope.bookMarkName = '';
	$scope.bookMarkUrl = '';
    };
    
    $scope.removeMarkItem = function(user, index) {
	var bookMarkId = user.bookmark[index].id;
	user.bookmark.splice(index, 1);
	deleteBookMark(bookMarkId);
    }

    function getItem(key) {
	return localStorageService.get(key);
    }

    function postBookMark(bookMarkName, bookMarkUrl) {
	$http.post('/bookmark', {'name': bookMarkName, 'url': bookMarkUrl}).
	    success(function(data, status, headers, config) {
	    }).
	    error(function(data, status, headers, config) {
	    });
    }
    
    function deleteBookMark(bookMarkId) {
	$http.delete('/bookmark/' + bookMarkId).
	    success(function(data, status, headers, config) {
	    }).
	    error(function(data, status, headers, config) {
	    });
    }
});
