function updateSlider( id,value ) {
	var minEltLabel = document.getElementById(id + "-min-label");
	if(minEltLabel != null) {
//	var values = $('#' + id + '-slider').slider("getValue");
	var min = value[0];
	var max = value[1];
	document.getElementById(id + "-min-label").innerHTML = min;
	document.getElementById(id + "-max-label").innerHTML = max;
	}
}
function createSlider( id ) {
	var minElt = document.getElementById(id + "Min");
	if(minElt != null) {
		var min = document.getElementById(id + "Min").value;
		var max = document.getElementById(id + "Max").value;
		var currMin = document.getElementById(id + "CurrentMin").value;
		var currMax = document.getElementById(id + "CurrentMax").value;
		$("#" + id + "-slider").slider({
			min: parseInt(min),
			max: parseInt(max),
			value: [parseInt(currMin), parseInt(currMax)]}).on('slide',function(event, ui) {
				updateSlider( id,event.value );
			}).on('slideStop',function(event, ui) {
				var urlPattern = document.getElementById(id + "Url").value;
				var url = urlPattern.replace("_min_",document.getElementById(id + "-min-label").innerHTML);
				url = url.replace("_max_",document.getElementById(id + "-max-label").innerHTML);
				if(url.match("ajax")=="ajax") {
					call(url,"mainContent");
				} else {
					window.open(url,"_self");
				}
			});
		updateSlider(id,[parseInt(currMin), parseInt(currMax)]);
	}
}

function bindSliders() {
createSlider("age");
createSlider("weight");
createSlider("height");
createSlider("date");

}