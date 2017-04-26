<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
 <head>
  <style>

.login-page {
  width: 360px;
  padding: 8% 0 0;
  margin: auto;
}
.form {
  position: relative;
  z-index: 1;
  background: #FFFFFF;
  max-width: 360px;
  margin: 0 auto 100px;
  padding: 45px;
  text-align: center;
  box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.2), 0 5px 5px 0 rgba(0, 0, 0, 0.24);
}
.form input {
  font-family: "Tahoma", sans-serif;
  outline: 0;
  background: #f2f2f2;
  width: 100%;
  border: 0;
  margin: 0 0 15px;
  padding: 15px;
  box-sizing: border-box;
  font-size: 14px;
}
.form button {
  font-family: "Tahoma", sans-serif;
  outline: 0;
  background: #4CAF50;
  width: 100%;
  border: 0;
  padding: 15px;
  color: #FFFFFF;
  font-size: 14px;
  -webkit-transition: all 0.3 ease;
  transition: all 0.3 ease;
  cursor: pointer;
}
.form button:hover,.form button:active,.form button:focus {
  background: #43A047;
}
.form .message {
  margin: 15px 0 0;
  color: #b3b3b3;
  font-size: 12px;
}
.form .message a {
  color: #4CAF50;
  text-decoration: none;
}
.form .register-form {
  display: none;
}
.container {
  position: relative;
  z-index: 1;
  max-width: 300px;
  margin: 0 auto;
}
.container:before, .container:after {
  content: "";
  display: block;
  clear: both;
}
.container .info {
  margin: 50px auto;
  text-align: center;
}
.container .info h1 {
  margin: 0 0 15px;
  padding: 0;
  font-size: 36px;
  font-weight: 300;
  color: #1a1a1a;
}
.container .info span {
  color: #4d4d4d;
  font-size: 12px;
}
.container .info span a {
  color: #000000;
  text-decoration: none;
}
.container .info span .fa {
  color: #EF3B3A;
}
body {
  background: #76b852; /* fallback for old browsers */
  background: -webkit-linear-gradient(right, #76b852, #8DC26F);
  background: -moz-linear-gradient(right, #76b852, #8DC26F);
  background: -o-linear-gradient(right, #76b852, #8DC26F);
  background: linear-gradient(to left, #76b852, #8DC26F);
  font-family: "Roboto", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;      
}
  </style>
  <script>
   function convertFormToJSON(form){
	    var array = jQuery(form).serializeArray();
	    var json = {};
	    
	    jQuery.each(array, function() {
	        json[this.name] = this.value || '';
	    });
	    
	    return json;
   }
   function handleRegisterResponse(res) {
	   if (res)
		   alert('User registered successfully!');
	   else
		   alert('User exists, please enter another user code');
   }
   function handleLoginResponse(errorMessage) {
	   if (errorMessage === "None")
		   window.location.reload();
	   else
		   alert(errorMessage);
   }
   function register() {
	   var formData = convertFormToJSON($("#register-form"));
	   $.ajax({
		   type: "POST",
		   url: "users",
		   data: JSON.stringify(formData),
		   success: function(data){handleRegisterResponse(data);},
		   dataType: "json",
		   contentType : "application/json"
		 });
   }
   function login() {
	   var formData = convertFormToJSON($("#login-form"));
	   $.ajax({
		   type: "POST",
		   url: "login",
		   data: JSON.stringify(formData),
		   success: function(data){handleLoginResponse(data);},
		   contentType : "application/json"
		 });
   }
   function toggleForms() {
	   $('form').animate({height: "toggle", opacity: "toggle"}, "slow"); 
   }
  </script>
 </head>
 <body>
	<div class="login-page">
		<div class="form">
			<form id="register-form" class="register-form" onsubmit="return false;"> 
				<input type="text" name="username" placeholder="username" /> 
				<input type="password" name="password" placeholder="password" />
				<input type="text" name="name" placeholder="name" />
				<input type="text" name="email" placeholder="email" />
				<button onclick="register()">Register</button>
				<p class="message">Already registered? <a href="javascript:toggleForms()">Sign In</a></p>
			</form>
			
			<form id="login-form" class="login-form" onsubmit="return false;">
				<input type="text" name="username" placeholder="username" />
				<input type="password" name="password" placeholder="password" />
				<button onclick="login()">Login</button>
				<p class="message">Not registered? <a href="javascript:toggleForms()">Create an account</a></p>
			</form>
		</div>
	</div>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.js"></script>
</body>
</html>
