<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>  
    <title>File Listener</title>
    <style>
      .pathname.ng-valid {
          background-color: lightgreen;
      }
      .pathname.ng-dirty.ng-invalid-required {
          background-color: red;
      }
      .pathname.ng-dirty.ng-invalid-minlength {
          background-color: yellow;
      }
      .path.ng-valid {
          background-color: lightgreen;
      }
      .path.ng-dirty.ng-invalid-required {
          background-color: red;
      }
      .path.ng-dirty.ng-invalid-minlength {
          background-color: yellow;
      }
      .threads.ng-valid {
          background-color: lightgreen;
      }
      .threads.ng-dirty.ng-invalid-required {
          background-color: red;
      }
      .threads.ng-dirty.ng-invalid-min {
          background-color: yellow;
      }


    </style>
     <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
     <link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
  </head>
  <body ng-app="myApp" class="ng-cloak">
      <div class="generic-container" ng-controller="PathController as ctrl">
          <div class="panel panel-default">
              <div class="panel-heading"><span class="lead">User Registration Form </span></div>
              <div class="formcontainer">
                  <form ng-submit="ctrl.submit()" name="myForm" class="form-horizontal">
                      <input type="hidden" ng-model="ctrl.path.id" />
                      <div class="row">
                          <div class="form-group col-md-12">
                              <label class="col-md-2 control-lable" for="file">Name</label>
                              <div class="col-md-7">
                                  <input type="text" ng-model="ctrl.path.name" name="pathname" class="pathname form-control input-sm" placeholder="Enter name of your path" required ng-minlength="5"/>
                                  <div class="has-error" ng-show="myForm.$dirty">
                                      <span ng-show="myForm.uname.$error.required">This is a required field</span>
                                      <span ng-show="myForm.uname.$error.minlength">Minimum length required is 3</span>
                                      <span ng-show="myForm.uname.$invalid">This field is invalid </span>

                                  </div>
                              </div>
                          </div>
                      </div>
                      <div class="row">
                          <div class="form-group col-md-12">
                              <label class="col-md-2 control-lable" for="file">Path</label>
                              <div class="col-md-7">
                                  <input type="text" ng-model="ctrl.path.path" name="path" class="path form-control input-sm" placeholder="Enter your path" required ng-minlength="3"/>
                                  <div class="has-error" ng-show="myForm.$dirty">
                                      <span ng-show="myForm.path.$error.required">This is a required field</span>
                                      <span ng-show="myForm.path.$error.minlength">Minimum length required is 3</span>
                                      <span ng-show="myForm.path.$invalid">This field is invalid </span>
                                      <span ng-show="myForm.path.$error.placeholder">This field is invalid </span>
                                  </div>
                              </div>
                          </div>
                      </div>
                      <div class="row">
                          <div class="form-group col-md-12">
                              <label class="col-md-2 control-lable" for="file">Threads</label>
                              <div class="col-md-7">
                                  <input type="numer" ng-model="ctrl.path.threads" name="threads" class="path form-control input-sm" placeholder="Number of threads for file sending" required ng-min="1" ng-max="10"/>
                                  <div class="has-error" ng-show="myForm.$dirty">
                                      <span ng-show="myForm.threads.$error.required">This is a required field</span>
                                      <span ng-show="myForm.threads.$error.integer">This value must be integer</span>
                                      <span ng-show="myForm.threads.$invalid">This field is invalid </span>
                                      <span ng-show="myForm.threads.$error.min || myForm.threads.$error.max">The value must be in range 1 to 100!</span>
                                  </div>
                              </div>
                          </div>
                      </div>

                      <div class="row">
                          <div class="form-actions floatRight">
                              <input type="submit" value="{{!ctrl.path.id ? 'Add' : 'Update'}}" class="btn btn-primary btn-sm" ng-disabled="myForm.$invalid">
                              <button type="button" ng-click="ctrl.reset()" class="btn btn-warning btn-sm" ng-disabled="myForm.$pristine">Reset Form</button>
                          </div>
                      </div>
                  </form>
              </div>
          </div>
          <div class="panel panel-default">
                <!-- Default panel contents -->
              <div class="panel-heading"><span class="lead">List of Paths </span></div>
              <div class="tablecontainer">
                  <table class="table table-hover">
                      <thead>
                          <tr>
                              <th>ID.</th>
                              <th>Name</th>
                              <th>Path</th>
                              <th>Threads</th>
                              <th width="20%"></th>
                          </tr>
                      </thead>
                      <tbody>
                          <tr ng-repeat="u in ctrl.paths">
                              <td><span ng-bind="u.id"></span></td>
                              <td><span ng-bind="u.name"></span></td>
                              <td><span ng-bind="u.path"></span></td>
                              <td><span ng-bind="u.threads"></span></td>
                              <td>
                              <button type="button" ng-click="ctrl.edit(u.id)" ng-disabled="{{u.started}}" class="btn btn-success custom-width">Edit</button>
                              <button type="button" ng-click="ctrl.remove(u.id)" class="btn btn-danger custom-width">Remove</button>
                              <button type="button" ng-click="ctrl.startPath(u)" class="btn btn-{{!u.started ? 'success' : 'danger'}} custom-width">{{!u.started ? 'Start' : 'Stop'}}</button>
                              </td>
                          </tr>
                      </tbody>
                  </table>
              </div>
          </div>
      </div>
      
      <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.4/angular.js"></script>
      <script src="<c:url value='/static/js/app.js' />"></script>
      <script src="<c:url value='/static/js/service/path_service.js' />"></script>
      <script src="<c:url value='/static/js/controller/path_controller.js' />"></script>
  </body>
</html>