
var app = angular.module("myShoppingList", ['googlechart','kendo.directives']); 
app.controller("myCtrl", function($scope,$http) {
	
	$scope.usersarray = []
	$scope.datesArray = []
	$scope.linkssArray = []
	 $scope.runs = []
	$scope.amountOfLinks = []
	$scope.fillBacgronud = []
	$scope.borderBacgronud = []
	$scope.severity = []
	$scope.squere ='sendUrl'; 
	$scope.shailink ="https://www.shaikimchi.com/test.json"; 
	
	$scope.isBar =true; 
	
   $scope.adrasses = {};
	$scope.dashBoard = true;
    $scope.names =[];
	$scope.HIGH24HOUR =[];
	$scope.LOW24HOUR =[];
	
	
	
	
   $http.get("https://min-api.cryptocompare.com/data/top/totalvolfull?limit=10&tsym=USD")
  .then(function(response) {
      $scope.coins = response.data.Data;
	  angular.forEach($scope.coins, function (value, key) { 
                $scope.names.push(value.CoinInfo.FullName); 
				$scope.HIGH24HOUR.push(value.DISPLAY.USD.HIGH24HOUR.replace("$", "").replace(",", "").trim()); 
				$scope.LOW24HOUR.push(value.DISPLAY.USD.LOW24HOUR.replace("$", "").replace(",", "").trim()); 
            });
  });
  
  $scope.createLinarChart=function(){
		
			var ctxL = document.getElementById("lineChart").getContext('2d');
			var myLineChart = new Chart(ctxL, {
			type: 'line',
			data: {
			labels: $scope.runs,
			datasets: [{
			label: "Links",
			data: $scope.amountOfLinks,
			backgroundColor: [
			'rgba(105, 0, 132, .2)',
			],
			borderColor: [
			'rgba(200, 99, 132, .7)',
			],
			borderWidth: 2
			},
			{
			label: "severity",
			data: $scope.severity,
			backgroundColor: [
			'rgba(0, 137, 132, .2)',
			],
			borderColor: [
			'rgba(0, 10, 130, .7)',
			],
			borderWidth: 2
			}
			]
			},
			options: {
			responsive: true
			}
			});
	}
	
  $scope.createChart=function(){

var ctxB = document.getElementById("barChart").getContext('2d');
var myBarChart = new Chart(ctxB, {
type: 'bar',
data: {
labels: $scope.runs,
datasets: [{
label: '# of Votes',
data: $scope.amountOfLinks,
backgroundColor: $scope.fillBacgronud,
borderColor: $scope.borderBacgronud,
borderWidth: 1
}]
},
options: {
scales: {
yAxes: [{
ticks: {
beginAtZero: true
}
}]
}
}
});

}

  
        var chart1 = {};	
		chart1.type = "GeoChart";
    

chart1.data = [
          ['Country', 'heelo'],   
          ['United States', 300],
          ['Brazil', 400],       
          ['France', 600],
          ['RU', 700]
];



chart1.options = {
  width: 600,
  height: 400,
  chartArea: {left:10,top:10,bottom:0,height:"100%"},
  colorAxis: {colors: ['blue', 'red']},
  displayMode: 'regions'
};

chart1.formatters = {
  number : [{
    columnNum: 1,
    pattern: " #,##0.00"
  }]
};

$scope.chart = chart1;

	$http.get('http://localhost:80/requests').then(function(response) {
        $scope.allRequsts = response.data;
		$scope.requstCounter = 0;		  
		angular.forEach($scope.allRequsts, function (requst, key) {
         requst.report.severityAmount = 0;
         angular.forEach(requst.report.processedData, function(pdata, key){
            requst.report.severityAmount = requst.report.severityAmount +pdata.severity
         });
   $scope.requstCounter =  $scope.requstCounter +1;
   requst.id = $scope.requstCounter ;		
	$scope.runs.push(requst.id);
	$scope.amountOfLinks.push(requst.report.processedData.length);
	$scope.fillBacgronud.push('rgba(54, 162, 235, 0.2)');
	$scope.borderBacgronud.push('Purple');
	$scope.severity.push(requst.report.severityAmount);
		    $http.get("http://ip-api.com/json/"+requst.report.mainIp).then(function(response) {
           $scope.ipDetails = response.data;		  
		   var tempc = $scope.ipDetails.country
	       var tempN = 450;
	       var temp = [tempc, tempN];
		   chart1.data.push(temp);
  });		   
            });
			$scope.createChart();
			$scope.createLinarChart();
		 
		  
 });


$scope.OpenWindow= function(crawlerTypeName)  // custom function on click
{
       $scope.crawlerTypeName =crawlerTypeName; 
      $scope.squere ='sendUrl'; 
    //   $scope.win2.content("{ content: 'C:/Users/shaik/Desktop/final project/try.html'}");
       $scope.win2.setOptions({title:'Send crawler',width: '40%',height:'20%',position:{top:0}});
	   $scope.win2.center();
        $scope.win2.open();
};



$scope.createReport= function(json)  // custom function on click
{
	  $scope.usersarray = []
	  $scope.datesArray = []
	  $scope.linkssArray = []
      $scope.testJson = json;
      $scope.temphg = json.report.processedData;
		  $http.get("http://ip-api.com/json/"+$scope.testJson.report.mainIp).then(function(response) {
           $scope.ipDetails = response.data;
		   $scope.testJson.siteCountry = $scope.ipDetails.country
		   $scope.testJson.siteCity = $scope.ipDetails.city
		   $scope.testJson.lat = $scope.ipDetails.lat
		   $scope.testJson.lon = $scope.ipDetails.lon
  });
  
		  angular.forEach($scope.temphg, function (value, key) { 
		  angular.forEach(value.userNames, function (user, key) { 
		   if ($scope.usersarray.indexOf(user) == -1) {
			   $scope.usersarray.push(user);			   
		   }			   
            });
			angular.forEach(value.dates, function (date, key) { 
		   if ($scope.datesArray.indexOf(date) == -1) {
			   $scope.datesArray.push(date);			   
		   }			   
            });
			if ($scope.linkssArray.indexOf(value.url) == -1) {
			   $scope.linkssArray.push(value.url);			   
		   }	
				
            });
			
			$scope.squere ='afterCrawling'; 
			$scope.win2.setOptions({width: '90%',height:'90%',position:{top:0}});
			$scope.win2.center();
}
$scope.showReport= function()
{
	$scope.win2.setOptions({title:'Report',width: '90%',height:'90%',position:{top:0}});
			$scope.win2.center();
	$scope.squere ='loader'; 
	//$http.get($scope.shailink).then(function(response) {
		$http.post('http://localhost:80/crawler/'+$scope.crawlerTypeName+'?url='+$scope.adrasses.urlAdrass).then(function(response) {
		   $scope.jsonReport = response.data;
		   $scope.jsonReport.id = $scope.allRequsts.length +1
           $scope.jsonReport.report.severityAmount = 0;
           angular.forEach( $scope.jsonReport.report.processedData, function(pdata, key){
            $scope.jsonReport.report.severityAmount = $scope.jsonReport.report.severityAmount +pdata.severity
         });
		   $scope.allRequsts.push($scope.jsonReport);
		   $scope.runs.push($scope.jsonReport.id);
	       $scope.amountOfLinks.push($scope.jsonReport.report.processedData.length);
	       $scope.fillBacgronud.push('rgba(54, 162, 235, 0.2)');
	       $scope.borderBacgronud.push('Purple');
	       $scope.severity.push($scope.jsonReport.report.severityAmount);
		   $scope.createChart();
			$scope.createLinarChart();
		   $scope.createReport($scope.jsonReport);
		 
		  
  });
	   
}

$scope.showHistoryReport= function(ID)
{
	$scope.squere ='showReport'; 
	angular.forEach($scope.allRequsts, function (requst, key) { 
		   if (requst.id == ID) {
			   $scope.correctId = requst ;	
               $scope.createReport($scope.correctId);	   
               $scope.win2.center();  // open dailog in center of screen
               $scope.win2.open();			   
		   }			   
            });
	   
}
$scope.crawlerTab= function()  // custom function on click
{
	document.getElementById("dashboardTab").classList.remove('active');
   document.getElementById("crawlerTab").classList.add('active');
$scope.crawler = true;
$scope.dashBoard = false;
    
};

$scope.dashboardTab= function()  // custom function on click
{
	document.getElementById("crawlerTab").classList.remove('active');
   document.getElementById("dashboardTab").classList.add('active');
$scope.crawler = false;
$scope.dashBoard = true;
    
};
 
//bar

});
