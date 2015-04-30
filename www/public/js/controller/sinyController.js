var siny = angular.module('siny', []);
siny.controller('sinyCtrl', function($scope) {
    var bookMarkName = $scope.bookMarkName;
    var bookMarkUrl = $scope.bookMarkUrl;
    $scope.bookMarks = [];

    $scope.append = function (bookMarks, bookMarkName, bookMarkUrl) {
	bookMarks.push({'url': bookMarkUrl, 'name': bookMarkName});
	$scope.bookMarkName = '';
	$scope.bookMarkUrl = '';
    };
});
