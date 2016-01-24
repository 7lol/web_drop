'use strict';

App.factory('PathService', ['$http', '$q', function ($http, $q) {

    return {

        fetchAllPaths: function () {
            return $http.get('http://localhost:8080/path/')
                .then(
                    function (response) {
                        return response.data;
                    },
                    function (errResponse) {
                        console.error('Error while fetching Paths');
                        return $q.reject(errResponse);
                    }
                );
        },


        startPath: function (path) {
            return $http.post('http://localhost:8080/start/'+path.id)
                .then(
                    function (response) {
                        path.started=true;
                        return response.data;
                    },
                    function (errResponse) {
                        console.error('Error while fetching Paths');
                        return $q.reject(errResponse);
                    }
                );
        },

        stopPath: function (path) {
            return $http.post('http://localhost:8080/stop/'+path.id)
                .then(
                    function (response) {
                        path.started=false;
                        return response.data;
                    },
                    function (errResponse) {
                        console.error('Error while fetching Paths');
                        return $q.reject(errResponse);
                    }
                );
        },

        createPath: function (path) {
            return $http.post('http://localhost:8080/path/', path)
                .then(
                    function (response) {
                        return response.data;
                    },
                    function (errResponse) {
                        console.error('Error while creating Path');
                        return $q.reject(errResponse);
                    }
                );
        },

        updatePath: function (path, id) {
            return $http.put('http://localhost:8080/path/' + id, path)
                .then(
                    function (response) {
                        return response.data;
                    },
                    function (errResponse) {
                        console.error('Error while updating Path');
                        return $q.reject(errResponse);
                    }
                );
        },

        deletePath: function (id) {
            return $http.delete('http://localhost:8080/path/' + id)
                .then(
                    function (response) {
                        return response.data;
                    },
                    function (errResponse) {
                        console.error('Error while deleting Path');
                        return $q.reject(errResponse);
                    }
                );
        }

    };

}]);
