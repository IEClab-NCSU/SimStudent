/**
 * Referenced from http://stackoverflow.com/questions/7114087/html5-file-upload-to-java-servlet
 * SHRUTI
 */
var formData = new FormData();

	// Note - The main idea is to use a div ("dropbox")
	// that can accept the file when it is dragged and dropped on it.
	// There is an image ("preview_image") that can display the file that is submitted.
	window.onload = function() {
		var dropbox = document.getElementById("dropbox");
		//add event listeners for the dropbox element
		dropbox.addEventListener("dragenter", noop, false);
		dropbox.addEventListener("dragexit", noop, false);
		dropbox.addEventListener("dragover", noop, false);
		dropbox.addEventListener("drop", dropUpload, false);
	}
	
	// This event handler ensures there is no operation performed.
	function noop(event) {
		//stopPropagation stops the event from bubbling up the event chain
		event.stopPropagation();
		//preventDefault prevents the default action the browser makes on that event
		event.preventDefault();
	}

	// This is the event handler that takes care of the files dropped in the "dropbox"
	function dropUpload(event) {
		noop(event);
		var files = event.dataTransfer.files;
		// For each file dropped
		for (var i = 0; i < files.length; i++) {
			upload(files[i]);
		}
	}

	function upload(file) {
		document.getElementById("status").innerHTML = "Uploading " + file.name;
		
		// Append the file as form data to send as a POST request to the servlet
		formData.append("file", file);

		var reader = new FileReader();
		reader.onload = handleReaderLoad;
		reader.readAsDataURL(file);
		
		document.getElementById("filename").value = file.name;
	}

	// Keeps track of the progress of the file upload
	function uploadProgress(event) {
		// Note: doesn't work with async=false.
		var progress = Math.round(event.loaded / event.total * 100);
		document.getElementById("status").innerHTML = "Progress " + progress
				+ "%";
	}

	// Displays the response status from the servlet
	function uploadComplete(event) {
		document.getElementById("status").innerHTML = event.target.responseText;
	}

	// Displays the file in "preview_image"
	function handleReaderLoad(event) {
		var img = document.getElementById("preview_image");
		// The source of the image points to file path in the local system,
		// therby generating a file preview in the preview_image element
		img.src = event.target.result;
	}

	//Reads when user uses Upload modal to upload files
	function readURL(files) {
		for (var i = 0; i < files.length; i++) {
			upload(files[i]);
		}
	}
	
	function submitFile() {
		var title = document.getElementById("title").value;
		var description = document.getElementById("description").value;

		// append the file title and description to form data
		formData.append("title", title);
		formData.append("description", description);
		
		// send the form data as an asynchronous POST request to the servlet
		var xhr = new XMLHttpRequest();
		// add an event handler to keep track of the progress of the file upload
		xhr.upload.addEventListener("progress", uploadProgress, false);
		// add an event handler that displays the response status from the servlet 
		xhr.addEventListener("load", uploadComplete, false);
		xhr.open("POST", "UploadServlet", true); // If async=false, then you'll miss progress bar support.
		xhr.send(formData);
	}