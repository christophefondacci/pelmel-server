Pelmel.Slider = {
	computeHeight : function(value) {
		var valStr = value + ' cm / ';
		var feetVal = Math.floor(value * 0.0328084);
		var inchVal = Math.round(value * 0.3937008 - feetVal * 12);
		valStr = valStr + feetVal + " ' " + inchVal + "''";
		$("#heightValue").html(valStr);
		$("#inputHeight").val(value);
	},
	computeWeight : function(value) {
		var valStr = value + ' kg / ';
		var lbVal = Math.round(value * 2.204623);
		valStr = valStr + lbVal + " lbs";
		$("#weightValue").html(valStr);
		$("#inputWeight").val(value);
	},
	updateSlider : function(id, value) {
		var minEltLabel = document.getElementById(id + "-min-label");
		if (minEltLabel != null) {
			var min = value[0];
			var max = value[1];
			$("#" + id + "-min-label").html(min);
			$("#" + id + "-max-label").html(max);
		} else if ("weight" === id) {
			Pelmel.Slider.computeWeight(value);
		} else if ("height" === id) {
			Pelmel.Slider.computeHeight(value);
		} 
	},
	createSlider : function(id) {
		var slider = $("#" + id + "-slider").slider({});
		if (slider) {
			slider.on('slide', function(event, ui) {
				Pelmel.Slider.updateSlider(id, event.value);
			}).on(
					'slideStop',
					function(event, ui) {
						var urlElt = document.getElementById(id + "Url");
						if (urlElt) {
							var urlPattern = urlElt.value;
							var url = urlPattern
									.replace("_min_",
											document.getElementById(id
													+ "-min-label").innerHTML);
							url = url
									.replace("_max_",
											document.getElementById(id
													+ "-max-label").innerHTML);
							if (url.match("ajax") == "ajax") {
								call(url, "mainContent");
							} else {
								window.open(url, "_self");
							}
						}
					});
		}
	}
}

function bindSliders() {
	Pelmel.Slider.createSlider("age");
	Pelmel.Slider.createSlider("weight");
	Pelmel.Slider.createSlider("height");
	Pelmel.Slider.createSlider("date");

}