'use strict';

App.controller('PathController', ['$scope', 'PathService', function ($scope, PathService) {
    var self = this;
    self.path = {id: null, name: '', path: '', started: false};
    self.paths = [];

    self.fetchAllPaths = function () {
        PathService.fetchAllPaths()
            .then(
                function (d) {
                    self.paths = d;
                },
                function (errResponse) {
                    console.error('Error while fetching Currencies');
                }
            );
    };

    self.createPath = function (path) {
        PathService.createPath(path)
            .then(
                self.fetchAllPaths,
                function (errResponse) {
                    console.error('Error while creating Path.');
                    path.path='path doesnt exist or already on list';
                }
            );
    };

    self.startPath = function (path) {
        if (path.started){
            PathService.stopPath(path)
                .then(
                    self.fetchAllPaths,
                    function (errResponse) {
                        console.error('Error while starting Path.');
                    }
                );
            path.started = false;}
        else{
        PathService.startPath(path)
            .then(
                self.fetchAllPaths,
                function (errResponse) {
                    console.error('Error while starting Path.');
                }
            );
        path.started = true;}
    };

    self.updatePath = function (user, id) {
        PathService.updatePath(user, id)
            .then(
                self.fetchAllPaths,
                function (errResponse) {
                    console.error('Error while updating Path.');
                }
            );
    };

    self.deletePath = function (id) {
        PathService.deletePath(id)
            .then(
                self.fetchAllPaths(),
                function (errResponse) {
                    console.error('Error while deleting Path.');
                }
            );
    };

    self.fetchAllPaths();

    self.submit = function () {
        if (self.path.id == null) {
            console.log('Saving new Path', self.path);
            self.createPath(self.path);
        } else {
            self.updatePath(self.path, self.path.id);
            console.log('Updated Path with id ', self.path.id);
        }
        self.reset();
    };

    self.edit = function (id) {
        console.log('id to be edited', id);
        for (var i = 0; i < self.paths.length; i++) {
            if (self.paths[i].id == id) {
                self.path = angular.copy(self.paths[i]);
                break;
            }
        }
    };

    self.remove = function (id) {
        console.log('id to be deleted', id);
        for (var i = 0; i < self.paths.length; i++) {
            if (self.paths[i].id == id) {
                self.reset();
                break;
            }
        }
        self.deletePath(id);
    };


    self.reset = function () {
        self.user = {id: null, name: '', path: '',started: false};
        $scope.myForm.$setPristine(); //reset Form
    };

}]);
