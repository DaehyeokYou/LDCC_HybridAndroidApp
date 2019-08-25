"use strict";
var $document = $(document);
var $window = $(window);
var iscroll;
var actArrw = function() {
  $('#scroller-left,#scroller-right').addClass('active');
  0 === iscroll.x ? $("#scroller-left").removeClass("active")
      : iscroll.x === iscroll.maxScrollX &&
  $("#scroller-right").removeClass("active");
}


var selectedTopmenu = "top-menu0";

$document.ready(function() {
  $('#main-banner').click(function() {
    if(!($(this).attr("href"))){
      coupangApp.go('', 'homeBanner');
    }
  });

  var _menuIndex = $("#scroller li a").index($("." + selectedTopmenu));
  iscroll.goToPage(_menuIndex, 0, 0);
  actArrw();

  /* Flicking */
  if($('#todayshot-section').hasClass('isFlicking')){
    var panelTime;
    $("#todayshotList").touchSlider({
      speed : 400,
      flexible : false,
      autoplay : true,
      page : 1,
      initComplete : function (e) {
        var listSize =  $("#todayshotList ul li").size();
        $('#todayshot-section .pagenation span').text(listSize);
        $('#todayshotList img').load(function(){$window.trigger('resizeImage')});
      },
      counter : function (e) {
        $('#todayshot-section .pagenation em').text(e.current);
        clearInterval(panelTime);
        panelTime = setInterval(function(){$("#todayshotList").get(0).animate(-1,true)},3000);
      },
      custom : function (){
        clearInterval(panelTime);
      },
      onTouchEnd : function() {
//                    todayshotList_show_cnt++;
      }
    });

    panelTime = setInterval(function(){$("#todayshotList").get(0).animate(-1,true)},5000);

    $window.bind({
      resizeImage : function() {
        $("#todayshotList").height($('.todayshot-deal-unit').outerHeight(true));
        $window.trigger('floatingTitle');
      },
      load : function() {
        $("#todayshotList").height($('.todayshot-deal-unit').outerHeight(true));
      }
    });
  };
  /* //Flicking */
});

/* v3. script */
if($('#topMenu').size() > 0 && $('#scroller').size() > 0){
  var totalWidth = 0;
  var lists = $('#scroller li');

  lists.each(function(index) {
    totalWidth += $(this).width();
  });
  $('#scroller').width(totalWidth + lists.length);

  var scrollOption = {
    snap : 'li',
    scrollX : true,
    scrollY : false,
    eventPassthrough : true
  };
  if(navigator.userAgent.match(/iphone/i) ||
      navigator.userAgent.match(/ipad/i)){
    scrollOption.useTransform = false;
  }
  iscroll = new IScroll('#topMenu', scrollOption);
  iscroll.on('scrollEnd', actArrw);
  $('#scroller-left,#scroller-right').click(function() {
    var newXPos;
    if($(this).hasClass("scroller-arr-left")){
      newXPos = iscroll.currentPage.pageX - 3;
      newXPos = newXPos < 0 ? 0 : newXPos;
      iscroll.goToPage(newXPos ,0);
    }else{
      newXPos = iscroll.currentPage.pageX + 3;
      newXPos = newXPos <= iscroll.pages.length ? newXPos : iscroll.pages.length;
      iscroll.goToPage(newXPos ,0);
    }
  });
}