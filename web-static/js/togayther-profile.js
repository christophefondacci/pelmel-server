function computeHeight( value ) {
	var valStr = value + ' cm / ';
	var feetVal = Math.floor(value*0.0328084);
	var inchVal = Math.round(value*0.3937008-feetVal*12);
	valStr = valStr + feetVal + " ' " + inchVal + "''";
	document.getElementById("heightValue").innerHTML= valStr;
	document.getElementById("inputHeight").value=value;
}
function computeWeight( value ) {
	var valStr = value + ' kg / ';
	var lbVal = Math.round(value*2.204623);
	valStr = valStr + lbVal + " lbs";
	document.getElementById("weightValue").innerHTML= valStr;
	document.getElementById("inputWeight").value=value;
}

function convert(id, value) {
	if("weight" == id) {
		computeWeight(value);
	} else if( "height" == id) {
		computeHeight(value);
	}
} 
function updateSlider( id , val) {
//	var val = $('#' + id + '-slider').slider("getValue");
	convert(id, val);
}
function createSlider( id ) {
	var min = document.getElementById(id + "Min").value;
	var max = document.getElementById(id + "Max").value;
	var current = document.getElementById(id + "CurrentValue").value;
	if(current=="") {
		current=min;
	}
	$("#" + id + "-slider").slider({
		range: false,
		min: parseInt(min),
		max: parseInt(max),
		value: parseInt(current)
		}).on('slide',function(event, ui) {
			updateSlider( id, event.value);
		});
	updateSlider(id,current);
}

function bindSliders() {
createSlider("weight");
createSlider("height");
}