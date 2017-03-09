function adminLogin() {
	$("#wait").removeClass('hidden');
	$('#loginresponse').text('');
	$('#login').toggleClass('hidden');
	postLogin($('#loginform').serialize());
}

function loginSuccess() {
	$("#wait").addClass('hidden');
	window.location.assign('/admin/index');
}

function loginFail(data) {
	$("#wait").addClass('hidden');
	$('#loginresponse').text(data.responseText);
	$('#login').removeClass('hidden');
}

function logout() {
	$("#wait").removeClass('hidden');			
	postLogout();
}

function readyNav() {
    var menutoggle = $('#menu_toggle');
    var body = $('body');
    var leftcol = $('.left_col');
    var sidebar = $('#sidebar-menu');
    var sidebar_footer = $('.sidebar-footer');
    var URL = window.location;
    
    menutoggle.on('click', function() {

	    if (body.hasClass('nav-md')) {
	     	body.removeClass('nav-md').addClass('nav-sm');
	       	leftcol.removeClass('scroll-view').removeAttr('style');
	       	sidebar_footer.hide();
	        if (sidebar.find('li').hasClass('active')) {
	           	sidebar.find('li.active').addClass('active-sm').removeClass('active');
	        }
	     } else {
	       	body.removeClass('nav-sm').addClass('nav-md');
	        sidebar_footer.show();
	        if (sidebar.find('li').hasClass('active-sm')) {
	          	sidebar.find('li.active-sm').addClass('active').removeClass('active-sm');
	        }
	    }
    });
    
    sidebar.find('li ul').slideUp();
    sidebar.find('li').removeClass('active');
    sidebar.find('li').on('click', function(ev) {
	    var link = $('a', this).attr('href');
	    	// prevent event bubbling on parent menu
	    if (link) {
	    	ev.stopPropagation();
    	} else {
    		if ($(this).is('.active')) {
    			$(this).removeClass('active');
    			$('ul', this).slideUp();
	        } else {
	            sidebar.find('li').removeClass('active');
	            sidebar.find('li ul').slideUp();
	            $(this).addClass('active');
	            $('ul', this).slideDown();
	        }
	    }
    });
}

function activateUIElements() {
	
	// Close ibox function
	$('.close-link').click(function () {
	    var content = $(this).closest('div.x_panel');
	    content.remove();
	});
	
	// Collapse ibox function
	$('.collapse-link').click(function () {
	    var x_panel = $(this).closest('div.x_panel');
	    var button = $(this).find('i');
	    var content = x_panel.find('div.x_content');
	    content.slideToggle(200);
	    (x_panel.hasClass('fixed_height_390') ? x_panel.toggleClass('').toggleClass('fixed_height_390') : '');
	    (x_panel.hasClass('fixed_height_320') ? x_panel.toggleClass('').toggleClass('fixed_height_320') : '');
	    button.toggleClass('fa-chevron-up').toggleClass('fa-chevron-down');
	    setTimeout(function () {
	        x_panel.resize();
	    }, 50);
	});


	// Accordion
	$(function () {
	    $(".expand").on("click", function () {
	        $(this).next().slideToggle(200);
	        var expand = $(this).find(">:first-child");

	        if (expand.text() == "+") {
	            expand.text("-");
	        } else {
	            expand.text("+");
	        }
	    });
	});
	
}


// Static API Calls

function postLogin(postdata) {
	return $.ajax({
		type: "POST",
		async: true,
		data : postdata,
		url: "/login",
		xhrFields: {
			withCredentials: true
		},
		success: function(data) {
			loginSuccess();
		},
		error: function(data) {
			loginFail(data);
		}	
	});	
}

function postLogout() {
	return $.ajax({
		type: "POST",
		async: true,
		url: "/logout",
		xhrFields: {
			withCredentials: true
		},
		success: function(data) {
			window.location.assign('/');
		}
	});	
}
