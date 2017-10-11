$(document).ready(function() {

  // Socket connection
  var ws;
  // Configurable time to ask the server
  // for the rate.
  // For demo purpose it is set to: 30 seconds
  var tSchedule = 30000;
  // Reference to scheduled function
  var fTimeout;
  // Variable used to simulate movement
  var offset_simulation = 0;

  // Function to create the session id:
  //   - Source: https://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
  function guid() {
    function s4() {
      return Math.floor((1 + Math.random()) * 0x10000)
        .toString(16)
        .substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
  }
 
  // Function to round a float 
  function roundBy(decimalPlaces, num) {
    var scale = Math.pow(10, decimalPlaces);
    return Math.round(scale * num) / scale;
  };

  // Function to calculate the distance covered:
  //    - Source: http://www.movable-type.co.uk/scripts/latlong.html
  function calculateDistance(lat1, lat2, long1, long2) {
    // Earth radius in kilometers
    var R = 6378
    var x = (long2-long1) * Math.cos((lat1+lat2)/2);
    var y = (lat2-lat1);
    var d = Math.sqrt(x*x + y*y) * R;
    return d
  }

  // Function used to simulate movement:
  //    - Source: https://gis.stackexchange.com/questions/2951/algorithm-for-offsetting-a-latitude-longitude-by-some-amount-of-meters
  function calculateOffset(dn, de, lat, lon) {
    // Earth radius in kilometers
    var R = 6378
    var PI = 3.1416
    dLat = dn/R
    dLon = de/(R*Math.cos(PI*lat/180))
    return [lat + dLat * 180/PI, lon + dLon * 180/PI]
  }

  // Function to ask the server for the rate.
  function askRate() {
     // API to get the current location:
     //   ****************************************
     //   *** It is used only for demo purpose ***
     //   ****************************************
     // Change the string 'XXX' by your token
     $.getJSON('https://ipinfo.io/8.8.8.8/geo?token=XXX', function(response) {
         // ***********************************************************************
         // *** 'fack_dist' is used only for demo purpose, to simulate movement ***
         // ***********************************************************************
         if (typeof response.loc != 'undefined') {
             var loc = response.loc.split(',');
             var new_loc = calculateOffset(offset_simulation, offset_simulation, parseFloat(loc[0]), parseFloat(loc[1]))
             var latitude = new_loc[0]
             var lat_prev = latitude
             var longitude = new_loc[1]
             var long_prev = longitude
             offset_simulation = offset_simulation + 0.01
             // Set initial position
             if (!sessionStorage.getItem('lat_start')) {
                sessionStorage.setItem('lat_start', loc[0])
                sessionStorage.setItem('long_start', loc[1])
             }
             // Get previous latitude
             if (sessionStorage.getItem('lat_prev')) {
                 lat_prev = parseFloat(sessionStorage.getItem('lat_prev'))
             }
             sessionStorage.setItem('lat_prev', latitude.toString());
             // Get previous longitude
             if (sessionStorage.getItem('long_prev')) {
                 long_prev = parseFloat(sessionStorage.getItem('long_prev'))
             }
             sessionStorage.setItem('long_prev', longitude.toString());
             // Calculate total distance traveled
             var diffDistTotal = roundBy(2, calculateDistance(latitude, parseFloat(sessionStorage.getItem('lat_start')), longitude, parseFloat(sessionStorage.getItem('long_start'))))
             // Calculate the distance covered from last update
             var diffDistUpd = roundBy(2, calculateDistance(latitude, lat_prev, longitude, long_prev))
             // Get current time
             var current_time = new Date();
             // Set initial time
             if (!sessionStorage.getItem('time_start')) {
                 sessionStorage.setItem('time_start', current_time.getTime())
             }
             var time_prev = current_time
             // Get previous time
             if (sessionStorage.getItem('time_prev')) {
                 time_prev = new Date(parseFloat(sessionStorage.getItem('time_prev')))
             }
             sessionStorage.setItem('time_prev', current_time.getTime());
             // Total time difference in millisecond
             var diffTimeTotal = current_time - new Date(parseFloat(sessionStorage.getItem('time_start')))
             // Convert to minutes
             diffTimeTotal = roundBy(3, ((diffTimeTotal % 86400000) % 3600000) / 60000);
             // Time difference from last update in milliseconds
             var diffTimeUpd = current_time - time_prev
             // Convert to minutes
             diffTimeUpd = roundBy(3, ((diffTimeUpd % 86400000) % 3600000) / 60000); 
             // Get the session id
             var session_id = sessionStorage.getItem('session_id')
             // Send the data to the server
             var resMsg = new Object();
             resMsg.session = session_id;
             resMsg.totalDist = diffDistTotal;
             resMsg.updDist = diffDistUpd;
             resMsg.totalTime = diffTimeTotal;
             resMsg.updTime = diffTimeUpd;   
             sendMsg(JSON.stringify(resMsg));
             // Re-schedule the 'askRate'
             fTimeout = setTimeout(askRate, tSchedule)
         } else {
               console.log('Ups, there was a problem retrieving the location...')
         }
     });
  }

  // Function to connect to the server using a WSocket
  function connectWs() {
    // Test if there is not a current WSocket connection
    if (ws === undefined || (ws.readyState === ws.CLOSED)) {
      // Get the URL where to connect using a WSocket
      var endpoint = $('body').data('endpoint')
      // Set the session id
      var session_id = guid();
      sessionStorage.setItem('session_id', session_id);
      // Connect to the server using a WSocket
      ws = new WebSocket(endpoint);

      // Receiving the rate from the server
      ws.onmessage = function(event) {
        var server_data = JSON.parse(event.data);
        $('#rate').html('<b>The current rate is</b>: ' + server_data.rate)
        $('#distance_total').html('<b>The total distance covered is</b>: ' + server_data.totalDist + ' kms')
        $('#distance_upd').html('<b>The distance covered from the last update is</b>: ' + server_data.updDist + ' kms')
        $('#time_total').html('<b>The total time traveled is</b>: ' + server_data.totalTime + ' minutes')
        $('#time_upd').html('<b>The time traveled from the last update is</b>: ' + server_data.updTime + ' minutes')
      }

      // The WSocket connection is open
      ws.onopen = function(event) {
        console.log('WS opened...');
        // Reset the user display
        $('#rate').html('')
        $('#distance_total').html('')
        $('#distance_upd').html('')
        $('#time_total').html('')
        $('#time_upd').html('')
        $('#msg').html('')
        // Start to track the distance
        fTimeout = setTimeout(askRate, 0)
        // Change the controller button
        $('#btnc').addClass('btn-danger').removeClass('btn-success');
        $('#btnc').html('Stop')
      };

      // Closing the WSocket connection
      ws.onclose = function(event) {
        // Stop tracking
        clearTimeout(fTimeout);
        // Clear the local session storage
        sessionStorage.removeItem('session_id')
        sessionStorage.removeItem('lat_start')
        sessionStorage.removeItem('lat_prev')
        sessionStorage.removeItem('long_prev')
        sessionStorage.removeItem('long_start')
        sessionStorage.removeItem('time_prev')
        sessionStorage.removeItem('time_start')
        // Reset the simulation variable
        offset_simulation = 0
        // Change the controller button
        $('#btnc').addClass('btn-success').removeClass('btn-danger');
        $('#btnc').html('Start')
        $('#msg').html('*** FINISHED ***')
        console.log('Ws closed ...');
      };

      // There was an error with the WSocket connection
      ws.onerror = function(event) {
        console.log('There was a WS error');
        console.log(event);
      };

    } else {
        console.log('Trying to connect, but there is an open WSocket connection...');
    }
  }

  // Function to send a message through the WSocket
  function sendMsg(msg) {
    if (ws && (ws.readyState === ws.OPEN)) {
        ws.send(msg);
    }
  }

  // Function to close the WSocket
  function closeWs() {
    if (ws && (ws.readyState === ws.OPEN)) {
        ws.close();
    }
  }

  // Add click handler to the controller button
  $(document).on('click', '#button-parent button', (function(){
      this.blur();
      var btnClass = $(this).attr('class');
      if (btnClass == 'btn-success') {
          // Connect to the server using a WSocket
          connectWs();
      } else {
          closeWs()
      }
  }));

});
