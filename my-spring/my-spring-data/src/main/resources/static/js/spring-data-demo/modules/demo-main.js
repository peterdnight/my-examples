// http://requirejs.org/docs/api.html#packages
// Packages are not quite as ez as they appear, review the above
require.config({
	waitSeconds: 30,
	paths: {
		ace: BASE_URL + "webjars/ace-builds/1.4.11/src",
		jsYaml: BASE_URL + "webjars/js-yaml/3.14.0/dist",
	},
	shim: {

	},
	packages: []
});
//require( [ "preferences", "projects/project", "services/services", "performance/performance", "hosts", "browser/utils", "deployment/app-cluster-browser", "deployment/deployment-backlog" ], function ( preferences, projects, services, performance, hosts, utils, clusterBrowser, deploymentBacklog ) {
require([], function() {

	// Shared variables need to be visible prior to scope

	$(document).ready(function() {
		console.log("Document ready: loading modules 9 ....");

		let $loadingPanel = $("#loading-project-message");
		$("div", $loadingPanel).text("Loading Application");
		$loadingPanel.css("visibility", "visible");
		deferredLoadingToEnableJquerySelectors();
	});

	let mainModules = [""];
	function deferredLoadingToEnableJquerySelectors() {
		require(mainModules, function() {

			console.log("\n\n modules loading complete \n\n");

			$(document).ready(function() {
				initialize();
			})

			function initialize() {
				console.log("\n\n\n --------  Starting module initialization  2-------- \n\n\n");

//				$("table a.csap-link").click(function(e) {
//					e.preventDefault();
//					var url = $(this).attr('href');
//					window.open(url, '_blank');
//				});
				CsapCommon.configureCsapAlertify();

				// alertify.csapWarning( "started" ) ;



				let $numberOfEmployeesToAdd = $("#number-of-employees");




				$numberOfEmployeesToAdd.change(function() {

					let numberSelected = $numberOfEmployeesToAdd.val();

					if (numberSelected == 0) {
						return;
					}


					let parameters = {
						number: numberSelected
					};

					console.log(`parameters: `, parameters);

					let url = BASE_URL + "test-data";
					$.post(url, parameters)

						.done(function(jsonResponse) {
							alertify.csapInfo(JSON.stringify(jsonResponse, null, "\t"));
							//console.log( `inlineEdit{} `, fileContents ) ;
						})

						.fail(function(jqXHR, textStatus, errorThrown) {
							alertify.alert("Failed Operation: " + jqXHR.statusText, "Contact support");
						}, 'json');

					$numberOfEmployeesToAdd.val(0);


				});
				
				let $showAllEmployees = $("#show-all-employees,#show-all-birthdays,#show-all-birthdays-pageable") ;
				
				
				$showAllEmployees.click(function( e ) {
					e.preventDefault();

					let parameters = {
						pageSize: $("#page-size").val(),
						pageNumber:  $("#page-number").val()
					};

					console.log(`parameters: `, parameters);

					let url = $(this).attr("href");
			        $.getJSON(
			                url ,
			                parameters )
			
			                .done( function ( userJson ) {
			                    alertify.csapInfo(JSON.stringify(userJson, null, "\t"));
			                } )
			
			                .fail( function ( jqXHR, textStatus, errorThrown ) {
			
			                    alertify.csapWarning("Failed Operation: " + jqXHR.statusText + "Contact support");
			                } ) ;
			                


				});
				
				
				


			}



		});

	}

});