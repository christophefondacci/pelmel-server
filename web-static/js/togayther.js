
function call( callUrl, elementId ) {
	jQuery.get(callUrl,null,function(data) { 
			$("#" +elementId).html(data); Pelmel.bindForms(); 
	} );
}

function clearPoints(markersArray) {
	for(var i in markersArray) {
		markersArray[i].setMap(null);
	}
}
function computeCentralPoint(markersArray) {
	return computeBounds(markersArray).getCenter();
}
function computeBounds(markersArray) {
	var latlngbounds = new google.maps.LatLngBounds();
	for(var i in markersArray) {
		var p = markersArray[i].position;
		if(p.lat() != 0 && p.lng() != 0) {
			latlngbounds.extend(markersArray[i].position);
		}
	}
	return latlngbounds;
}
function setfocus(obj, defaultText) {
	if(defaultText == obj.value) {
		obj.value='';
	} 
}
function lostfocus(obj, defaultText) {
	if(trim(obj.value) == '') {
		obj.value=defaultText;
	} 
}

function trim (myString) { 
	return myString.replace(/^\s+/g,'').replace(/\s+$/g,'') 
} 
var placeSubmit = false;

function check( elt ) {
//	var checked = document.getElementById(checkId).checked;
//	document.getElementById(checkId).checked = !checked;
//	var className = 'tag tagwidth';
//	if(!checked) {
//		className = 'selected-tag tagwidth';
//	}
//	document.getElementById('a-' + checkId).className = className;
	var id = elt.id;
	var tagId = id.substring(id.indexOf("-")+1);
	var checkbox = $("#" + tagId);
	var checked = checkbox.attr("checked");
	checkbox.attr("checked",!checked);
	$(elt).parent().toggleClass("search-facet-selected");
	
}
function checkCommentTag(checkId,url) {
	$("#tag-" + checkId).toggleClass("selected-tag");
	call(url,null);
}
function bindDefaultOverlays() {

}
function moveMedia(callUrl) {
	jQuery.get(callUrl,null,function(data) { $(".profile-media-container, .images").html(data); Pelmel.handleMediaList(); initOverlays();} );
}


function lookup( callUrl ) {
	jQuery.get(callUrl,null,function(data) { document.getElementById('cityDefinition').innerHTML = data; });
}
function like(url,target) {
	$('#likeaction').attr("src","//static.pelmelguide.com/images/loading.gif");
	call(url, target);
}
function waitCall(url, target, loadTarget ) {
	$(loadTarget).html("<img border='0' height='24' src='//static.pelmelguide.com/images/loading.gif' alt='Loading....'/>");
	call(url,target);
}
// Geocoding and map features
var point;
var shadowIcon; 
var geocoder;
var placeMarker;
var placeMap;
function createGMap() {
	shadowIcon = new google.maps.MarkerImage('//static.pelmelguide.com/images/markers/shadow-marker.png',new google.maps.Size(40,36),new google.maps.Point(0,0),new google.maps.Point(12,32));
	geocoder = new google.maps.Geocoder();
	var lat = document.getElementById("place-edit-lat").value;
	var lng = document.getElementById("place-edit-lng").value;
	var placeType = document.getElementById("placeType").value;
	point = new google.maps.LatLng(lat,lng);
	var myOptions = {
      zoom: 16,
      center: point,
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      disableDefaultUI: true,
      zoomControl: true
    };
	placeMap = new google.maps.Map(document.getElementById("place-map"),
            myOptions);
	var icon = new google.maps.MarkerImage('//static.pelmelguide.com/images/markers/' + placeType + '-marker.png', new google.maps.Size(24,32),new google.maps.Point(0,0),new google.maps.Point(12,32));
	placeMarker = new google.maps.Marker({position:new google.maps.LatLng(lat, lng), icon : icon, shadow : shadowIcon, map : placeMap, draggable: true});
	google.maps.event.addListener(placeMarker,"dragend",function() {
		document.getElementById("place-edit-lat").value=placeMarker.getPosition().lat();
		document.getElementById("place-edit-lng").value=placeMarker.getPosition().lng();
		document.getElementById('place-address').value="";
		geocode();
	});
}
function geocode(callback) {
	geocoder = new google.maps.Geocoder();
	$('#place-edit-spinner').html("<img border='0' height='15' src='//static.pelmelguide.com/images/loading.gif' alt='Loading...'/>");
	var address= $('#place-address').val();
	var city = $('#city').val();
	var fullAddr = address + ", " + city;
//	if(address == "") {
//		var lat=  document.getElementById("place-edit-lat").value;
//		var lng = document.getElementById("place-edit-lng").value;
//		if(lat!="" && lng !="") {
//			geocoder.geocode({"latLng" : new google.maps.LatLng(lat,lng)}, function(results,status) {
//				document.getElementById('place-edit-spinner').innerHTML="";
//				if(status == google.maps.GeocoderStatus.OK) {
//					document.getElementById("place-address").value = results[0].formatted_address;
//				}
//			});
//		}
//	} else {
		geocoder.geocode({'address' : fullAddr}, function(results, status) {
			$('#place-edit-spinner').html("");
			if(status == google.maps.GeocoderStatus.OK) {
				var loc = results[0].geometry.location;
				$("#place-edit-lat").val(loc.lat());
				$("#place-edit-lng").val(loc.lng());
			}
			callback();
		});
//	}
}
function mosaicInfo(obj,row,col) {
	$("#info-" + row + "-" + col).css("top: " + document.getElementById('cursorY').value)
	$("#info-" + row + "-" + col).css("left: " + document.getElementById('cursorX').value)
	$("#info-" + row + "-" + col).toggle();
}


function addProperty(prefix, addedHeight) {
	var templateNode = $("#" + prefix+'-template');
	var template = templateNode.html();
	var container = $("#" + prefix + "-container");
	var height = Number(container.height())+Number(addedHeight);
	var heightVal = height + "px"
	container.append(template);	
	container.animate({ "height" : heightVal },500);
}
function handleDialogs() {
	$(".tag").click(function() {
		check(this);
	});
	$(".control-add").click(function() {
		var id = this.id;
		var prefix = id.substring(0,id.indexOf("-"));
		var height = id.substring(id.lastIndexOf("-")+1);
		addProperty(prefix,height);
	});
	$("input#city").typeahead([{
		remote:"/ajaxSuggestCity.action?type=CITY&term=%QUERY", 
		valueKey:"userLabel", 
		template : "<p><img src='{{imageUrl}}'><strong>{{mainText}}</strong> - {{suffix}}</p>",
		engine: Hogan
	}]);
	$("input#city").on('typeahead:selected', function (object, datum) {
		$("#cityValue").val(datum.value);
	});
	$("input#place").typeahead([{
		name:"Places",
		remote:"/ajaxSuggestCity.action?type=PLAC&term=%QUERY", 
		valueKey:"userLabel", 
		template : "<p><img src='{{imageUrl}}'><strong>{{mainText}}</strong> - {{suffix}}</p>",
		engine: Hogan
	}]);
	$("input#place").on('typeahead:selected', function (object, datum) {
		$("#placeId").val(datum.value);
	});
	$("input#startDate").datepicker({format: 'yyyy/mm/dd'});
	$("input#endDate").datepicker({format: 'yyyy/mm/dd'});
	$("#update-place-form").submit(function(event) {
		if(!placeSubmit) {
			placeSubmit= true;
			geocode(function() {
				$("#update-place-form").submit();
			});
			return false;
		} else {
			return true;
		}
	});
}


function showAction(showMap) {
	$(".action-map-controls").css("opacity","1");
	var mapOpacity = 0;
	var imgOpacity= 1;
	if(showMap) {
		mapOpacity = 1;
		imgOpacity= 0;
		$(".action-map-container").css("display","block");
		$("#map-controls").css("display","block");
		google.maps.event.trigger(map, "resize");
	} else {
		$(".action-image-container").css("display","block");
		$("#img-controls").css("display","block");
	}
	$(".action-map-container").animate({ opacity: mapOpacity},300);
	$("#map-controls").animate({ opacity: mapOpacity},300);
	$("#img-controls").animate({ opacity: imgOpacity},300);
	$(".action-image-container").animate({ opacity: imgOpacity},300,function() {
		if(showMap){
			$(".action-image-container").css("display","none");
			$("#img-controls").css("display","none");
		} else {
			$(".action-map-container").css("display","none");
			$("#map-controls").css("display","none");
		}
	});
}
function hideAllActions() {
	var hideAll = function() {
		$(".action-map-controls").css("display","none");
		$(".action-image-container").css("display","none");
		$(".action-map-container").css("display","none");
	};
	$(".action-map-controls").animate({ opacity: 0},300,hideAll);
	$(".action-map-container").animate({ opacity: 0},300,hideAll);
	$(".action-image-container").animate({ opacity: 0},300,hideAll);
}
function initOverlays() {
	$('body').on('hidden.bs.modal', '.modal', function (e) {
		  $(this).removeData('bs.modal');
		  $(e.target).find(".modal-content > *").remove();
		});
	$('body').on("loaded.bs.modal",'.modal',function() {
		handleDialogs();
		initAddMedia();
	});
}


function handleMyMessages() {
	$(".msg-user").click(function() {
		$("#msg-contents").html("<img border='0' height='15' src='//static.pelmelguide.com/images/loading.gif' alt='Loading...'/>");
		var url = $(this).attr('href');
		jQuery.get(url,null, function(data) {
			$("#msg-contents").html(data);
			Pelmel.bindForms();
		});
		return false;
	});
	$(".pagination").click(function() {
		$(this).html("<img border='0' height='15' src='//static.pelmelguide.com/images/loading.gif' alt='Loading...'/>");
		var url = $(this).attr('href');
		jQuery.get(url,null, function(data) {
			$("#msg-list").html(data);
			handleMyMessages();
		});
		return false;
	});
}
function initialize() {}
var Pelmel = {
	markersarray : [],
	map : {},
	imageOverlay : false,
	initTooltips: function() {
		if(!Pelmel.mobileDetect.any()) {
			$(".search-tooltip").each(function() {
				$(this).parent().eq(0).hoverIntent({
						timeout:150,
						over : function() {
							var current = $('.search-tooltip:eq(0)', this);
							current.fadeIn(100);
						} ,
						out: function() {
							var current = $('.search-tooltip:eq(0)', this);
							current.fadeOut(100);
						}
				});
			});
		}
		initOverlays();
	},
	initMap: function(options) {
	    var myOptions = {
	      zoom: options.zoom,
	      center: options.center,
	      mapTypeId: google.maps.MapTypeId.ROADMAP,
	      disableDefaultUI: true,
	      zoomControl: true,
	      scaleControl: true,
	      streetViewControl: true
	    };
	    var container = document.getElementById("map");
	    if(container) {
	    	Pelmel.map = new google.maps.Map(container,myOptions);
	    	Pelmel.infowindow=new google.maps.InfoWindow({content: 'Loading...'});
	    }
	    Pelmel.handleSearch();
	},
	allMarkers: [],
	/**
	 * Clears all markers from map
	 */
	clearMap: function() {
		for(var i = 0 ; i < Pelmel.allMarkers.length ; i++) {
			Pelmel.allMarkers[i].setMap(null);
		}
		Pelmel.allMarkers.length=0;
		Pelmel.markersarray.length=0;
	},
	zIndex:0,
	addPoint : function (lat,lng,title,icon, shadowIcon,infoUrl,itemKey,ignoreFromList) {
		var marker = new google.maps.Marker({position:new google.maps.LatLng(lat,lng), title :title,icon: icon, map: Pelmel.map, pelmelItemKey:itemKey,zIndex:Pelmel.zIndex++});
		if(!ignoreFromList) {
			Pelmel.markersarray.push(marker);
		}
		Pelmel.allMarkers.push(marker);
		google.maps.event.addListener(marker,'click',function() {
			jQuery.get(infoUrl ? infoUrl : "/mapInfo.action?geoKey=" + itemKey,null,function(data) {
				Pelmel.infowindow.setContent(data);
				Pelmel.infowindow.open(Pelmel.map,marker);
			});
		});
	},
	mapTimer: function() {
		if(!Pelmel.mapTimerIndex) {
			Pelmel.map.setZoom(10);
			Pelmel.mapTimerIndex = 0;
		} 
		if(Pelmel.markersarray.length>0) {
			var marker = Pelmel.markersarray[Pelmel.mapTimerIndex];
			var pos = marker.getPosition();
			Pelmel.map.panTo(pos);
			// Removing any previous effect
//			if(Pelmel.lastMarkerKey) {
//				$("#activity-" + Pelmel.lastMarkerKey).removeClass("selected-activity");
//			}
			// Selecting corresponding activity
			var nextActivity = $("#activity-" + marker.pelmelItemKey); //.addClass("selected-activity");
			var nextTop = nextActivity.position().top;
			var nextHeight = nextActivity.height();
			var nextMargin = parseInt(nextActivity.css("margin-top"));
			var scroll = nextActivity.parent().scrollTop();
			// Detecting if we need to scroll to make it visible
			var maxVisiblePosition = nextActivity.parent().height()-nextHeight;
			if(nextTop+nextMargin>maxVisiblePosition) {
				// Scrolling parent so current item will become the first one after scroll
				nextActivity.parent().animate({
					scrollTop: maxVisiblePosition + scroll
				});
			} else if(Pelmel.mapTimerIndex == 0) {
				// Scrolling to top at the end
				nextActivity.parent().animate({
					scrollTop: 0
				});
			}
			$("#activity-highlighter").animate({
				top: (nextTop+nextMargin+scroll) + "px",
				height: nextHeight
			});
			$("#activity-highlighter > .highlight-caret").animate({
				top: (nextHeight-25)/2
			});
			Pelmel.lastMarkerKey = marker.pelmelItemKey;
		}
		Pelmel.mapTimerIndex = (Pelmel.mapTimerIndex+1)%Pelmel.markersarray.length;
		Pelmel.mapTimerVar = setTimeout(function() {
			Pelmel.mapTimer();
		},5000);
	},
	bindDropdowns: function(type,subtype) {
//		$('.dropdown').each(function () {
//			$(this).parent().eq(0).hoverIntent({
//				timeout: 100,
//				over: function () {
//					var current = $('.dropdown:eq(0)', this);
//					current.slideDown(100);
//					Pelmel.handleDropDown(current, current.attr("id"),type,subtype);
//				},
//				out: function () {
//					var current = $('.dropdown:eq(0)', this);
//					current.fadeOut(200);
//				}
//			});
//		});
//		Pelmel.handleDropDownLinks();
		enlargeEnabled = true;
		minimizeEnabled = false;
		
		$(document).ready(function() {
			Pelmel.handleMapControls();
	   		Pelmel.handleSearch();
		});
	},
	handleDropDown: function (elt, id, calType, subType) {
		if(id) {
			// Extracting id from last '-' fragment of the id
			var sepIndex = id.lastIndexOf("-");
			// If found
			if(sepIndex>0) {
				// Extracting type
				var parentGeoId = id.substring(sepIndex+1);
				// But ignoring prefix
				var typeIndex = id.indexOf("-");
				if(typeIndex == sepIndex) {
					typeIndex = -1;
				}
				// Getting type
				var type = id.substring(typeIndex+1,sepIndex);
				if(type!="menu") {
					$('#' + id).each(function() {
						jQuery.get("/ajaxGeoList.action?type=" + calType + "&subType=" + subType + "&parent=" + parentGeoId +"&level=" + type ,null,function(data) {
							var parent = elt;
							parent.html("");
							jQuery.each(data, function() {
								var sizedLabel = this.label;
								if(this.label.length>20) {
									sizedLabel = this.label.substring(0,20) + "...";
								}
								parent.append("<li class='geo-option'><a href='" + this.url + "'>" + sizedLabel+ "<span class='li-count'>" + this.value + "</span></a></li>");
							});
							Pelmel.handleDropDownLinks();
						});
					});
				}
			}
		}
	},
	handleDropDownLinks: function() {
		$('.dropdown a').hover(function () {
			$(this).stop(true).animate({paddingLeft: '8px'}, {speed: 100, easing: 'easeOutBack'});
		}, function () {
			$(this).stop(true).animate({paddingLeft: '0'}, {speed: 100, easing: 'easeOutBounce'});
		});
	},
	initOverview: function() {
		Pelmel.initToolbar();
		$(".tile-info").each(function() {
			$(this).parent().eq(0).hoverIntent({
					timeout:150,
					over : function() {
						var current = $('.tile-info:eq(0)', this);
						current.fadeIn(100);
					} ,
					out: function() {
						var current = $('.tile-info:eq(0)', this);
						current.fadeOut(100);
					}
			});
		});
		Pelmel.handleMediaList();
	},
	handleMediaList: function () {
		$(".user-media").hoverIntent({
			timeout: 0,
			over: function() {
				$(".media-actions",this).fadeIn(50);
			},
			out: function() {
				$(".media-actions",this).fadeOut(50);
			} 
		});
	},
	initToolbar: function() {
		initOverlays();
		$(".tool-pictures").click(function() {
			var backgroundSize = $("div.ov-image").css("background-size");
			if(backgroundSize === "cover") {
				$("div.ov-image").css("background-size","contain");
			} else {
				$("div.ov-image").css("background-size","cover");
			}
			showAction(false);
		});
		$(".tool-tmap").click(function() {
			showAction(true);
		});
		$(".tool-tmsg").click(function() {
			$(".msg-instant-container").show();
		});
		$(".msg-instant-cancel").click(function() {
			$(".msg-instant-container").hide();
			return false;
		});
		$("a[rel='ajax-image']").click(function(e) {
			e.preventDefault();
			jQuery.get("/ajaxMedia.action?id=" + this.id, null, function(data) {
				if(!Pelmel.imageOverlay) {
					$("#image-container-1").html(data).animate({opacity:1},300);
					$("#image-container").animate({opacity:0},300);
				} else {
					$("#image-container").html(data);
					$("#image-container-1").animate({opacity:0},300);
					$("#image-container").animate({opacity:1},300);
				}
				Pelmel.imageOverlay=!Pelmel.imageOverlay;
			});
		});
		if(!Pelmel.mobileDetect.any()) {
			$(".tool").tooltip(); 
//			hoverIntent({
//				timeout:500, 
//				over : function() { 
//					$(".tooltip-" + Pelmel.lastId).hide();
//					$("#visible-edit").hide();
//					var tooltip = $(".tooltip-" + this.id);
//					var left =$(this).position().left+4;
//					tooltip.css("left",left);
//					tooltip.show();
//					Pelmel.lastId = this.id;
//				}, 
//				out : function() {
//					$(".tooltip-" + this.id).hide();
//				}
//			});
		}
	},
	bindForms: function() {
		var options = {
				target: '.msg-instant-container',
				url: '/ajaxSendMsg.action',
				success: function(responseText,statusText, xhr,$form) {
					$(".msg-instant-container").hide();
				}
		};
		jQuery('#sendMsg').ajaxForm(options);
		options = {
				target: '#msg-contents',
				url: '/ajaxSendFullMsg.action',
				success: function(responseText,statusText, xhr,$form) {
					Pelmel.bindForms();
				}
		};
		jQuery('#sendFullMsg').ajaxForm(options);
		options = {
				target: '#instantUserMsg',
				beforeSubmit: function(arr,$form,options) {
					$("#instant-post-spinner").html("<img class='nextIcon' height='15' src='//static.pelmelguide.com/images/loading.gif' alt='Loading...'/>");
				},
				url: '/ajaxSendInstantMsg.action',
				success: function(responseText,statusText, xhr,$form) {
					Pelmel.bindForms();
				}
		};
		jQuery('#instantMsgForm').ajaxForm(options);
		options = {
				target : "#comments-contents",
				url: "/postComment.action",
				beforeSubmit: function(arr,$form,options) {
					$("#comment-post-spinner").html("<img border='0' height='15' src='//static.pelmelguide.com/images/loading.gif' alt='Loading...'/>");
				},
				success: function(responseText,statusText, xhr,$form) {
					Pelmel.bindForms();
				}
		};
		$("#comment-post-form").ajaxForm(options);
	},
	bindLike: function(containerName) {
		$("#like").click(function() {
			jQuery.get(containerName, null, function() {
				var likeTag = $("#like");
				likeTag.toggleClass("like-selected");
				var like = $("#like-count");
				var count = parseInt(like.html());
				if(likeTag.is(".like-selected")) {
					like.html(count+1);
				} else {
					like.html(count-1);
				}
			});
		});
	},
	handleMapControls: function() {
		$(".map-enlarge").click(function() {
			if(enlargeEnabled) {
				center = Pelmel.map.getCenter();
				enlargeEnabled=false;
				$(".action-map-container").animate({
					"padding-left": "0px",
					"width": "90%",
					"top": "-335px",
					"height": "256px"
				},200, function() {
					google.maps.event.trigger(map, "resize");
					Pelmel.map.setCenter(center);
					minimizeEnabled=true;
				});
			}
		});
		$(".map-minimize").click(function() {
			if(minimizeEnabled) {
				center = Pelmel.map.getCenter();
				minimizeEnabled=false;
				$(".action-map-container").animate({
					"right": "10px",
					"width": "190px",
					"top": "-275px",
					"height": "196px"
				},200, function() {
					google.maps.event.trigger(map, "resize");
					Pelmel.map.setCenter(center);
					enlargeEnabled=true;
				});
			}
		});
		$(".map-close").click(hideAllActions);
	},
	handleSearch: function() {
		var searchType= $("input#searchType").val();
		Pelmel.autocompleteSearch(searchType, "search-geo");
		Pelmel.autocompleteSearch(searchType, "home-search-geo");
//		Pelmel.autocompleteSearch(searchType, "search-place", "PLAC");
	},
	labels : {
		"com" : {
			"Cities" : "Cities",
			"Places" : "Venues",
			"search.noResult" : "No result found.",
			"search.title" : "Results"
		},
		"fr" : {
			"Cities" : "Villes",
			"Places" : "Lieux",
			"search.noResult" : "Aucun résultat trouvé.",
			"search.title" : "Résultats"
		}
	},
	decodeString: function(code) {
		var index = location.host.lastIndexOf(".");
		if(index>=0 && index < (location.host.length-1)) {
			var ext = location.host.substring(index+1);
			var labels = Pelmel.labels[ext];
			if(!labels) {
				labels = Pelmel.labels["com"];
			}
			var l = labels[code];
			if(l) {
				return l;
			} else {
				return code;
			}
		}
	},
	autocompleteDatums : {
		"PLAC" : {
			labelCode:"Places",
			remote:"/ajaxSuggestCity.action?type=PLAC&term=%QUERY", 
			valueKey:"userLabel", 
			header:"<h3 class='search-section-title'>Places</h3>",
			template : "<p><img src='{{imageUrl}}'><strong>{{mainText}}</strong> - {{suffix}}</p>",
			engine: Hogan,
			limit:5
		},
		"CITY" : {
			labelCode:"Cities",
			remote:"/ajaxSuggestCity.action?type=CITY&term=%QUERY", 
			valueKey:"userLabel",
			header:"<h3 class='search-section-title'>Cities</h3>",
			template : "<p><img src='{{imageUrl}}'><strong>{{mainText}}</strong> - {{suffix}}</p>",
			engine: Hogan,
			limit:6
		}
	},
	autocompleteSearch: function(searchType,id,type) {
		var datums = [];
		if(type) {
			datums = [Pelmel.autocompleteDatums[type]];
		} else {
			datums = [Pelmel.autocompleteDatums["PLAC"],Pelmel.autocompleteDatums["CITY"]];
		}
		for(var i = 0 ; i < datums.length ; i++) {
			datums[i].header = "<h3 class='search-section-title'>" + Pelmel.decodeString(datums[i].labelCode) + "</h3>";
		}
		$("input#" + id).typeahead(datums);
		$("input#" +id).on('typeahead:selected', function (object, datum) {
			window.location=datum.url;
		});

//		$("input#" + id).autocomplete({
//			source : url, 
//			minLength:2, 
//			select: function(event, ui) {
//				if(ui.item.value.length==4) {
//				} else {
//				window.location=ui.item.url; //'/searchMap.action?geoKey=' + ui.item.value;
//				}
//				return false;
//			}, 
//			focus: function(event, ui) { 
//				return false; 
//			}, 
//			html: true,
//			open: function() {
//				$(this).autocomplete('widget').css('z-index',200);
//				return false;
//			}
//		});
	},
	displayMsg: function() {
		var current = document.getElementById('instantMsg').style.display;
		if(current == 'inline') {
			document.getElementById('instantMsg').style.display='none';
		} else {
			document.getElementById('instantMsg').style.display='inline';
		}
	},
	refreshMessages: function(timeout) {
		var timer = setTimeout("Pelmel.msgCall('/ajaxInstantRefreshTitle.action','instantMsgTitle'," + timeout + ");",timeout);
		var hasMsg = $('#hasMsg').html();
		if(hasMsg == "false") {
			var pageStyle = $("#pageStyle").val();
			var url = '/ajaxInstantRefreshBody.action';
			if(pageStyle != null) {
				url = url +"?pageStyle=" + pageStyle;
			}
			Pelmel.simpleCall(url,'instantMsgBody');
		}
	},
	msgCall: function ( callUrl, elementId, timeout ) {
		jQuery.get(callUrl,null,function(data) { 
			$('#' + elementId).html(data);
			Pelmel.refreshMessages(timeout);
		});
	},
	bindSendMsg: function () {
		options = {
			target : "#mainContent",
			url:"postMessage.action",
			beforeSubmit: function(arr,$form,options) {
				$("#message-post-spinner").html("<img border='0' height='15' src='//static.pelmelguide.com/images/loading.gif' alt='Loading...'/>");
			},
			success: function(responseText,statusText, xhr,$form) {
				Pelmel.bindForms();
				Pelmel.bindSendMsg();
			}
		}
		$("#message-post-form").ajaxForm(options);
	},
	simpleCall: function ( callUrl, elementId ) {
		jQuery.get(callUrl,null,function(data) { 
			$('#' + elementId).html(data); 
			Pelmel.bindForms(); 
		} );
	},
	msgInit: function() {
		if($("#hasMsg").length>0) {
			Pelmel.refreshMessages(10000);
		}
	},
	initSlideshow: function(container) {
		var imgContainer = $(container); 
		var children = imgContainer.children();
		if(imgContainer.length > 0 && children.length>0) {
			imgContainer.jqFancyTransitions({position:'top',direction:'left', width:750, height:312, navigation: true, links: true});	
		}
	}, 
	mobileDetect : {
	    Android: function() {
	        return navigator.userAgent.match(/Android/i);
	    },
	    BlackBerry: function() {
	        return navigator.userAgent.match(/BlackBerry/i);
	    },
	    iOS: function() {
	        return navigator.userAgent.match(/iPhone|iPad|iPod/i);
	    },
	    Opera: function() {
	        return navigator.userAgent.match(/Opera Mini/i);
	    },
	    Windows: function() {
	        return navigator.userAgent.match(/IEMobile/i);
	    },
	    any: function() {
	        return (Pelmel.mobileDetect.Android() || Pelmel.mobileDetect.BlackBerry() || Pelmel.mobileDetect.iOS() || Pelmel.mobileDetect.Opera() || Pelmel.mobileDetect.Windows());
	    }
	},
	centerImageVertically: function () {
    },
	focusComment:function (obj) {
    	$("#comment-text").addClass("focused-comment");
    },
	blurComment : function(obj) {
    	if(trim(obj.value) == '') {
    		$("#comment-text").removeClass("focused-comment");
    	}
    },
    /**
     * Handles the search field of the homepage
     */
    handleSearch: function() {
    	$("input.pelmel-search").keyup(function(event) {
    		if(Pelmel.searchTimer) {
    			clearTimeout(Pelmel.searchTimer);
    			Pelmel.searchTarget = event.target;
    		}
    		Pelmel.searchTimer = setTimeout(function() {
    			var searchText = $(Pelmel.searchTarget).val();
    			Pelmel.search(searchText);
    		},300);
    	})
    },
    search : function(searchText) {
    	
    	var activityContents = $("#activity-contents");
    	var activityTitle = $("#search-title");
		if(searchText.length > 2) {
			if(Pelmel.lastSearch==searchText.trim()) {
				return;
			}
			// Saving original contents
			if(!Pelmel.noSearchHtml) {
				Pelmel.noSearchHtml = activityContents.html();
				Pelmel.noSearchTitle = activityTitle.html();
				Pelmel.noSearchMarkers = Pelmel.allMarkers.slice(0);
			}
			// Displaying wait animation
			activityContents.html("<div class='wait'><img src='//static.pelmelguide.com/images/V3/wait.gif'></div>");
			// Querying
			jQuery.get("/ajaxSuggestCity?filterEmptyCities=true&term=" + encodeURIComponent(searchText),null,function(data) {
				var html = "";
				activityTitle.html(Pelmel.decodeString("search.title"));
				if(data && data.length>0) {
					// Processing every suggestion
					var imageUrl = "";
					var pointer = Pelmel.buildMapPointer();
					// Clearing all markers from map
					Pelmel.clearMap();
					// Removing any ongoing map animation
					clearTimeout(Pelmel.mapTimerVar);
					// Processing results
					for(var i = 0 ; i < data.length ; i++){
						var suggest = data[i];
						// If we got something displayable
						if(suggest.mainText && suggest.suffix) {
							// Generating HTML
							html+='<div class="media"><a class="pull-left" href="';
							html+=suggest.url;
							html+='"><img class="activity-thumb" src="';
							if(suggest.imageUrl) {
								imageUrl =suggest.imageUrl;
							} else {
								imageUrl ="//static.pelmelguide.com/images/V2/no-photo-small.png";
							}
							html+=imageUrl;
							html+='"></a>';
							html+='<div class="media-body"><div class="activity-body">';
							html+='<a class="activity-user" href="';
							html+=suggest.url;
							html+='">'+suggest.mainText + '</a>';
							html+='</div>';
							html+='<span class="timestamp">';
							html+=suggest.suffix;
							html+='</span>';
							html+='</div></div>';
//							function (lat,lng,title,icon, shadowIcon,infoUrl,ignoreFromList) {
							
							// Generating a marker for this entry
							var thumbMarker = Pelmel.buildMapThumbMarker(imageUrl);
							Pelmel.addPoint(suggest.lat,suggest.lng,suggest.mainText,thumbMarker,null,suggest.url,suggest.value);
							Pelmel.addPoint(suggest.lat,suggest.lng,null,pointer,null,null,suggest.value, true);
						}
					}
					Pelmel.map.fitBounds(computeBounds(Pelmel.markersarray));
				} else {
					html= Pelmel.decodeString("search.noResult");
				}
				$("#activity-contents").html(html);
			});
		} else {
			// Restoring initial contents if searched string is not long enough
			if(Pelmel.noSearchHtml) {
				activityContents.html(Pelmel.noSearchHtml);
				activityTitle.html(Pelmel.noSearchTitle);
				Pelmel.clearMap();
				for(var i = 0 ; i < Pelmel.noSearchMarkers.length ; i++) {
					Pelmel.noSearchMarkers[i].setMap(Pelmel.map);
				}
				Pelmel.allMarkers = Pelmel.noSearchMarkers.slice(0);
				Pelmel.map.fitBounds(computeBounds(Pelmel.allMarkers));
			}
		}
		Pelmel.lastSearch = searchText.trim();
		if(Pelmel.searchTimer) {
			clearTimeout(Pelmel.searchTimer);
			Pelmel.searchTimer = null;
		}
    },
    buildMapThumbMarker: function(thumbUrl) {
    	return {url:thumbUrl,size:new google.maps.Size(50, 50),origin:new google.maps.Point(0,0),anchor: new google.maps.Point(15, 20),scaledSize: new google.maps.Size(30, 30)};
    }, 
    buildMapPointer: function() {
    	return {url:'//static.pelmelguide.com/images/V3/mapMarkerFg.png',size:new google.maps.Size(34, 40),origin:new google.maps.Point(0,0),anchor: new google.maps.Point(17,20)}; //,scaledSize: new google.maps.Size(24, 30)};
    }
};
