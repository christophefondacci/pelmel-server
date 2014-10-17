var jcrop_api;
function previewChanged(coords) {
	$("#previewCropX").val(coords.x);
	$("#previewCropY").val(coords.y);
	$("#previewCropW").val(coords.w);
	$("#previewCropH").val(coords.h);
}
function initPreview() {
	$("#preview-image").Jcrop({
		onChange: previewChanged,
		onSelect: previewChanged,
		aspectRatio: 2.1428
	},function() {
		jcrop_api = this;
	});
	$("#formMediaKey").val($("#mediaKey").val());
}
function initAddMedia() {
	$("input:file").change(function() {
		uploadFile();
	});
	$("#constrainRatio").change(function() {
		if(jcrop_api) {
			jcrop_api.setOptions(this.checked ? { aspectRatio: 2.083 } : {aspectRatio: 0});
		}
	});
}
function uploadProgress(evt) {
//	document.getElementById('progress-container').style.display='inherit';
  if (evt.lengthComputable) {
    var percentComplete = Math.round(evt.loaded * 100 / evt.total);
    $('#progress').css('width',percentComplete.toString() + '%').attr('aria-valuenow',percentComplete);
    if(percentComplete < 100) {
    	$('#progress').html(percentComplete.toString() + '%');
    } else {
    	$('#progress').html('Processing...');
    }
  }
  else {
//    document.getElementById('progressNumber').innerHTML = 'n/a';
  }
}

function uploadComplete(evt) {
  /* This event is raised when the server send back a response */
	$(".image-preview").html(evt.currentTarget.response);
	$("#progress-container").toggleClass('active');
	$("#progress-container").toggleClass('progress-stripped');
	document.getElementById('progress').innerHTML = 'Done';
	initPreview();
}

function uploadFailed(evt) {
  alert("There was an error attempting to upload the file.");
}

function uploadCanceled(evt) {
  alert("The upload has been canceled by the user or the browser dropped the connection.");
}  
function uploadFile() {
  var xhr= new XMLHttpRequest();
  var fd = new FormData(document.getElementById('addMediaForm'));

  /* event listners */
  xhr.upload.addEventListener("progress", uploadProgress, false);
  xhr.addEventListener("load", uploadComplete, false);
  xhr.addEventListener("error", uploadFailed, false);
  xhr.addEventListener("abort", uploadCanceled, false);
  /* Be sure to change the url below to the url of your upload server side script */
  xhr.open("POST", "/addMedia.action");
  xhr.send(fd);
}
