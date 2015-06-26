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

    $scope.append = function (user, bookMarkName, bookMarkUrl, bookMarkTab) {
    	var bm = {'url': bookMarkUrl, 'name': bookMarkName}
    	if (user.bookmark[bookMarkTab] === undefined) {
			user.bookmark[bookMarkTab] = [];
			user.bookmark[bookMarkTab].push(bm);
    	} else {
			user.bookmark[bookMarkTab].push(bm);
		}

		postBookMark(bookMarkName, bookMarkUrl, "AU4wBCzRoaYyUtPpf_xo");

		$scope.bookMarkName = '';
		$scope.bookMarkUrl = '';
		$scope.bookMarkTab = '';
    };

    $scope.addTab = function (tabName) {
    	postTab(tabName);
    };

    $scope.removeMarkItem = function(user, tab, index) {
		var bookMarkId = user.bookmark[tab][index].id;
		user.bookmark[tab].splice(index, 1);
		deleteBookMark(bookMarkId);
    }

    function getItem(key) {
		return localStorageService.get(key);
    }

    function postTab(tabName) {
		$http.post('/tab', {'name': tabName}).
		    success(function(data, status, headers, config) {
		})
		.error(function(data, status, headers, config) {
		});
    }

    function postBookMark(bookMarkName, bookMarkUrl, bookMarkTabId) {
		$http.post('/bookmark', {'name': bookMarkName, 'url': bookMarkUrl, 'tabId': bookMarkTabId}).
		    success(function(data, status, headers, config) {
		})
		.error(function(data, status, headers, config) {
		});
    }
    
    function deleteBookMark(bookMarkId) {
		$http.delete('/bookmark/' + bookMarkId).
		    success(function(data, status, headers, config) {
		})
		.error(function(data, status, headers, config) {
		});
	}
});
