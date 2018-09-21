(function () {

    init();
    
    function init() {
        $("signup-btn").addEventListener('click', register);
        $("signup-form").addEventListener('submit', function(event){event.preventDefault();});
        
    }

    // -----------------------------------
    // Signup
    // -----------------------------------

    function register() {
        
        var username = $("username").value;
        var email = $("email").value;
        var password = $("password").value;
        password = md5(username + md5(password));

        // The request parameters
        var url = './signup';
        var req = JSON.stringify({
            username : username,
            email: email,
            password : password,
        });
        if ($("signup-form").checkValidity()) {
            ajax('POST', url, req, 
                function(res) {
                    window.location.href = ("index.html");
                },
                
                function(res) {
                    var result = JSON.parse(res);
                    $("signup-error").innerHTML = result.status;
                }
            );
        } else {
            console.log("validation failed");
        }
         
    }
    

    /**
     * A helper function that creates a DOM element <tag options...>
     * 
     * @param tag
     * @param options
     * @returns
     */
    function $(tag, options) {
        if (!options) {
            return document.getElementById(tag);
        }

        var element = document.createElement(tag);

        for (var option in options) {
            if (options.hasOwnProperty(option)) {
                element[option] = options[option];
            }
        }
        return element;
    }
    
    function hideElement(element) {
        element.style.display = 'none';
    }
    
    function showElement(element, style) {
        var displayStyle = style ? style : 'block';
        element.style.display = displayStyle;
    }

    /**
     * AJAX helper
     *
     * @param method -
     *            GET|POST|PUT|DELETE
     * @param url -
     *            API end point
     * @param callback -
     *            This the successful callback
     * @param errorHandler -
     *            This is the failed callback
     */
    function ajax(method, url, data, callback, errorHandler, credentials) {
        var xhr = new XMLHttpRequest();
        xhr.open(method, url, true);
        xhr.onload = function() {
            if (xhr.status === 200) {
                callback(xhr.responseText);
            } else {
                errorHandler(xhr.responseText);
            }
        };

        xhr.withCredentials = credentials;
        xhr.onerror = function() {
            console.error("The request couldn't be completed.");
        };

        if (data === null) {
            xhr.send();
        } else {
            xhr.setRequestHeader("Content-Type",
                "application/json;charset=utf-8");
            xhr.send(data);
        }
    }

})();