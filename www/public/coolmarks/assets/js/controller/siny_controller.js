var siny = angular.module('siny', ['LocalStorageModule']);

siny.config(function (localStorageServiceProvider) {
    localStorageServiceProvider
	.setPrefix('coolmarks')
	.setStorageType('sessionStorage')
	.setNotify(true, true)
});

siny.controller('sinyCtrl', function($scope, localStorageService) {
    var bookMarkName = $scope.bookMarkName;
    var bookMarkUrl = $scope.bookMarkUrl;
    
    $scope.user = getItem("chengpohi") || { bookMarks: [] };

    $scope.append = function (user, bookMarkName, bookMarkUrl) {
	user.bookMarks.push({'url': bookMarkUrl, 'name': bookMarkName});
	$scope.bookMarkName = '';
	$scope.bookMarkUrl = '';
	localStorageService.set("chengpohi", user);
    };
    
    function getItem(key) {
	return localStorageService.get(key);
    }
});
