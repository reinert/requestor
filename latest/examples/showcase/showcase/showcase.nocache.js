function showcase(){
  var $intern_0 = 'bootstrap', $intern_1 = 'begin', $intern_2 = 'gwt.codesvr.showcase=', $intern_3 = 'gwt.codesvr=', $intern_4 = 'showcase', $intern_5 = 'startup', $intern_6 = 'DUMMY', $intern_7 = 0, $intern_8 = 1, $intern_9 = 'iframe', $intern_10 = 'javascript:""', $intern_11 = 'position:absolute; width:0; height:0; border:none; left: -1000px;', $intern_12 = ' top: -1000px;', $intern_13 = 'CSS1Compat', $intern_14 = '<!doctype html>', $intern_15 = '', $intern_16 = '<html><head><\/head><body><\/body><\/html>', $intern_17 = 'undefined', $intern_18 = 'DOMContentLoaded', $intern_19 = 50, $intern_20 = 'script', $intern_21 = 'javascript', $intern_22 = 'Failed to load ', $intern_23 = 'moduleStartup', $intern_24 = 'scriptTagAdded', $intern_25 = 'moduleRequested', $intern_26 = 'meta', $intern_27 = 'name', $intern_28 = 'showcase::', $intern_29 = '::', $intern_30 = 'gwt:property', $intern_31 = 'content', $intern_32 = '=', $intern_33 = 'gwt:onPropertyErrorFn', $intern_34 = 'Bad handler "', $intern_35 = '" for "gwt:onPropertyErrorFn"', $intern_36 = 'gwt:onLoadErrorFn', $intern_37 = '" for "gwt:onLoadErrorFn"', $intern_38 = '#', $intern_39 = '?', $intern_40 = '/', $intern_41 = 'img', $intern_42 = 'clear.cache.gif', $intern_43 = 'baseUrl', $intern_44 = 'showcase.nocache.js', $intern_45 = 'base', $intern_46 = '//', $intern_47 = 'user.agent', $intern_48 = 'webkit', $intern_49 = 'safari', $intern_50 = 'msie', $intern_51 = 10, $intern_52 = 11, $intern_53 = 'ie10', $intern_54 = 9, $intern_55 = 'ie9', $intern_56 = 8, $intern_57 = 'ie8', $intern_58 = 'gecko', $intern_59 = 'gecko1_8', $intern_60 = 2, $intern_61 = 3, $intern_62 = 4, $intern_63 = 'selectingPermutation', $intern_64 = 'showcase.devmode.js', $intern_65 = '70CCCC5A59F895B1265294BFA86BA2B1', $intern_66 = 'A3CFBEE5F417D3883E2626801AA9C403', $intern_67 = 'A9AE23DFEC7E5CB742F12B41DA7AE68B', $intern_68 = ':', $intern_69 = '.cache.js', $intern_70 = 'loadExternalRefs', $intern_71 = 'end', $intern_72 = 'http:', $intern_73 = 'file:', $intern_74 = '_gwt_dummy_', $intern_75 = '__gwtDevModeHook:showcase', $intern_76 = 'Ignoring non-whitelisted Dev Mode URL: ', $intern_77 = ':moduleBase', $intern_78 = 'head';
  var $wnd = window;
  var $doc = document;
  sendStats($intern_0, $intern_1);
  function isHostedMode(){
    var query = $wnd.location.search;
    return query.indexOf($intern_2) != -1 || query.indexOf($intern_3) != -1;
  }

  function sendStats(evtGroupString, typeString){
    if ($wnd.__gwtStatsEvent) {
      $wnd.__gwtStatsEvent({moduleName:$intern_4, sessionId:$wnd.__gwtStatsSessionId, subSystem:$intern_5, evtGroup:evtGroupString, millis:(new Date).getTime(), type:typeString});
    }
  }

  showcase.__sendStats = sendStats;
  showcase.__moduleName = $intern_4;
  showcase.__errFn = null;
  showcase.__moduleBase = $intern_6;
  showcase.__softPermutationId = $intern_7;
  showcase.__computePropValue = null;
  showcase.__getPropMap = null;
  showcase.__installRunAsyncCode = function(){
  }
  ;
  showcase.__gwtStartLoadingFragment = function(){
    return null;
  }
  ;
  showcase.__gwt_isKnownPropertyValue = function(){
    return false;
  }
  ;
  showcase.__gwt_getMetaProperty = function(){
    return null;
  }
  ;
  var __propertyErrorFunction = null;
  var activeModules = $wnd.__gwt_activeModules = $wnd.__gwt_activeModules || {};
  activeModules[$intern_4] = {moduleName:$intern_4};
  showcase.__moduleStartupDone = function(permProps){
    var oldBindings = activeModules[$intern_4].bindings;
    activeModules[$intern_4].bindings = function(){
      var props = oldBindings?oldBindings():{};
      var embeddedProps = permProps[showcase.__softPermutationId];
      for (var i = $intern_7; i < embeddedProps.length; i++) {
        var pair = embeddedProps[i];
        props[pair[$intern_7]] = pair[$intern_8];
      }
      return props;
    }
    ;
  }
  ;
  var frameDoc;
  function getInstallLocationDoc(){
    setupInstallLocation();
    return frameDoc;
  }

  function setupInstallLocation(){
    if (frameDoc) {
      return;
    }
    var scriptFrame = $doc.createElement($intern_9);
    scriptFrame.src = $intern_10;
    scriptFrame.id = $intern_4;
    scriptFrame.style.cssText = $intern_11 + $intern_12;
    scriptFrame.tabIndex = -1;
    $doc.body.appendChild(scriptFrame);
    frameDoc = scriptFrame.contentDocument;
    if (!frameDoc) {
      frameDoc = scriptFrame.contentWindow.document;
    }
    frameDoc.open();
    var doctype = document.compatMode == $intern_13?$intern_14:$intern_15;
    frameDoc.write(doctype + $intern_16);
    frameDoc.close();
  }

  function installScript(filename){
    function setupWaitForBodyLoad(callback){
      function isBodyLoaded(){
        if (typeof $doc.readyState == $intern_17) {
          return typeof $doc.body != $intern_17 && $doc.body != null;
        }
        return /loaded|complete/.test($doc.readyState);
      }

      var bodyDone = isBodyLoaded();
      if (bodyDone) {
        callback();
        return;
      }
      function onBodyDone(){
        if (!bodyDone) {
          bodyDone = true;
          callback();
          if ($doc.removeEventListener) {
            $doc.removeEventListener($intern_18, onBodyDone, false);
          }
          if (onBodyDoneTimerId) {
            clearInterval(onBodyDoneTimerId);
          }
        }
      }

      if ($doc.addEventListener) {
        $doc.addEventListener($intern_18, onBodyDone, false);
      }
      var onBodyDoneTimerId = setInterval(function(){
        if (isBodyLoaded()) {
          onBodyDone();
        }
      }
      , $intern_19);
    }

    function installCode(code_0){
      var doc = getInstallLocationDoc();
      var docbody = doc.body;
      var script = doc.createElement($intern_20);
      script.language = $intern_21;
      script.src = code_0;
      if (showcase.__errFn) {
        script.onerror = function(){
          showcase.__errFn($intern_4, new Error($intern_22 + code_0));
        }
        ;
      }
      docbody.appendChild(script);
      sendStats($intern_23, $intern_24);
    }

    sendStats($intern_23, $intern_25);
    setupWaitForBodyLoad(function(){
      installCode(filename);
    }
    );
  }

  showcase.__startLoadingFragment = function(fragmentFile){
    return computeUrlForResource(fragmentFile);
  }
  ;
  showcase.__installRunAsyncCode = function(code_0){
    var doc = getInstallLocationDoc();
    var docbody = doc.body;
    var script = doc.createElement($intern_20);
    script.language = $intern_21;
    script.text = code_0;
    docbody.appendChild(script);
  }
  ;
  function processMetas(){
    var metaProps = {};
    var propertyErrorFunc;
    var onLoadErrorFunc;
    var metas = $doc.getElementsByTagName($intern_26);
    for (var i = $intern_7, n = metas.length; i < n; ++i) {
      var meta = metas[i], name_0 = meta.getAttribute($intern_27), content;
      if (name_0) {
        name_0 = name_0.replace($intern_28, $intern_15);
        if (name_0.indexOf($intern_29) >= $intern_7) {
          continue;
        }
        if (name_0 == $intern_30) {
          content = meta.getAttribute($intern_31);
          if (content) {
            var value_0, eq = content.indexOf($intern_32);
            if (eq >= $intern_7) {
              name_0 = content.substring($intern_7, eq);
              value_0 = content.substring(eq + $intern_8);
            }
             else {
              name_0 = content;
              value_0 = $intern_15;
            }
            metaProps[name_0] = value_0;
          }
        }
         else if (name_0 == $intern_33) {
          content = meta.getAttribute($intern_31);
          if (content) {
            try {
              propertyErrorFunc = eval(content);
            }
             catch (e) {
              alert($intern_34 + content + $intern_35);
            }
          }
        }
         else if (name_0 == $intern_36) {
          content = meta.getAttribute($intern_31);
          if (content) {
            try {
              onLoadErrorFunc = eval(content);
            }
             catch (e) {
              alert($intern_34 + content + $intern_37);
            }
          }
        }
      }
    }
    __gwt_getMetaProperty = function(name_0){
      var value_0 = metaProps[name_0];
      return value_0 == null?null:value_0;
    }
    ;
    __propertyErrorFunction = propertyErrorFunc;
    showcase.__errFn = onLoadErrorFunc;
  }

  function computeScriptBase(){
    function getDirectoryOfFile(path){
      var hashIndex = path.lastIndexOf($intern_38);
      if (hashIndex == -1) {
        hashIndex = path.length;
      }
      var queryIndex = path.indexOf($intern_39);
      if (queryIndex == -1) {
        queryIndex = path.length;
      }
      var slashIndex = path.lastIndexOf($intern_40, Math.min(queryIndex, hashIndex));
      return slashIndex >= $intern_7?path.substring($intern_7, slashIndex + $intern_8):$intern_15;
    }

    function ensureAbsoluteUrl(url_0){
      if (url_0.match(/^\w+:\/\//)) {
      }
       else {
        var img = $doc.createElement($intern_41);
        img.src = url_0 + $intern_42;
        url_0 = getDirectoryOfFile(img.src);
      }
      return url_0;
    }

    function tryMetaTag(){
      var metaVal = __gwt_getMetaProperty($intern_43);
      if (metaVal != null) {
        return metaVal;
      }
      return $intern_15;
    }

    function tryNocacheJsTag(){
      var scriptTags = $doc.getElementsByTagName($intern_20);
      for (var i = $intern_7; i < scriptTags.length; ++i) {
        if (scriptTags[i].src.indexOf($intern_44) != -1) {
          return getDirectoryOfFile(scriptTags[i].src);
        }
      }
      return $intern_15;
    }

    function tryBaseTag(){
      var baseElements = $doc.getElementsByTagName($intern_45);
      if (baseElements.length > $intern_7) {
        return baseElements[baseElements.length - $intern_8].href;
      }
      return $intern_15;
    }

    function isLocationOk(){
      var loc = $doc.location;
      return loc.href == loc.protocol + $intern_46 + loc.host + loc.pathname + loc.search + loc.hash;
    }

    var tempBase = tryMetaTag();
    if (tempBase == $intern_15) {
      tempBase = tryNocacheJsTag();
    }
    if (tempBase == $intern_15) {
      tempBase = tryBaseTag();
    }
    if (tempBase == $intern_15 && isLocationOk()) {
      tempBase = getDirectoryOfFile($doc.location.href);
    }
    tempBase = ensureAbsoluteUrl(tempBase);
    return tempBase;
  }

  function computeUrlForResource(resource){
    if (resource.match(/^\//)) {
      return resource;
    }
    if (resource.match(/^[a-zA-Z]+:\/\//)) {
      return resource;
    }
    return showcase.__moduleBase + resource;
  }

  function getCompiledCodeFilename(){
    var answers = [];
    var softPermutationId = $intern_7;
    function unflattenKeylistIntoAnswers(propValArray, value_0){
      var answer = answers;
      for (var i = $intern_7, n = propValArray.length - $intern_8; i < n; ++i) {
        answer = answer[propValArray[i]] || (answer[propValArray[i]] = []);
      }
      answer[propValArray[n]] = value_0;
    }

    var values = [];
    var providers = [];
    function computePropValue(propName){
      var value_0 = providers[propName](), allowedValuesMap = values[propName];
      if (value_0 in allowedValuesMap) {
        return value_0;
      }
      var allowedValuesList = [];
      for (var k in allowedValuesMap) {
        allowedValuesList[allowedValuesMap[k]] = k;
      }
      if (__propertyErrorFunction) {
        __propertyErrorFunction(propName, allowedValuesList, value_0);
      }
      throw null;
    }

    providers[$intern_47] = function(){
      var ua = navigator.userAgent.toLowerCase();
      var docMode = $doc.documentMode;
      if (function(){
        return ua.indexOf($intern_48) != -1;
      }
      ())
        return $intern_49;
      if (function(){
        return ua.indexOf($intern_50) != -1 && (docMode >= $intern_51 && docMode < $intern_52);
      }
      ())
        return $intern_53;
      if (function(){
        return ua.indexOf($intern_50) != -1 && (docMode >= $intern_54 && docMode < $intern_52);
      }
      ())
        return $intern_55;
      if (function(){
        return ua.indexOf($intern_50) != -1 && (docMode >= $intern_56 && docMode < $intern_52);
      }
      ())
        return $intern_57;
      if (function(){
        return ua.indexOf($intern_58) != -1 || docMode >= $intern_52;
      }
      ())
        return $intern_59;
      return $intern_15;
    }
    ;
    values[$intern_47] = {gecko1_8:$intern_7, ie10:$intern_8, ie8:$intern_60, ie9:$intern_61, safari:$intern_62};
    __gwt_isKnownPropertyValue = function(propName, propValue){
      return propValue in values[propName];
    }
    ;
    showcase.__getPropMap = function(){
      var result = {};
      for (var key in values) {
        if (values.hasOwnProperty(key)) {
          result[key] = computePropValue(key);
        }
      }
      return result;
    }
    ;
    showcase.__computePropValue = computePropValue;
    $wnd.__gwt_activeModules[$intern_4].bindings = showcase.__getPropMap;
    sendStats($intern_0, $intern_63);
    if (isHostedMode()) {
      return computeUrlForResource($intern_64);
    }
    var strongName;
    try {
      unflattenKeylistIntoAnswers([$intern_49], $intern_65);
      unflattenKeylistIntoAnswers([$intern_59], $intern_66);
      unflattenKeylistIntoAnswers([$intern_53], $intern_67);
      strongName = answers[computePropValue($intern_47)];
      var idx = strongName.indexOf($intern_68);
      if (idx != -1) {
        softPermutationId = parseInt(strongName.substring(idx + $intern_8), $intern_51);
        strongName = strongName.substring($intern_7, idx);
      }
    }
     catch (e) {
    }
    showcase.__softPermutationId = softPermutationId;
    return computeUrlForResource(strongName + $intern_69);
  }

  function loadExternalStylesheets(){
    if (!$wnd.__gwt_stylesLoaded) {
      $wnd.__gwt_stylesLoaded = {};
    }
    sendStats($intern_70, $intern_1);
    sendStats($intern_70, $intern_71);
  }

  processMetas();
  showcase.__moduleBase = computeScriptBase();
  activeModules[$intern_4].moduleBase = showcase.__moduleBase;
  var filename = getCompiledCodeFilename();
  if ($wnd) {
    var devModePermitted = !!($wnd.location.protocol == $intern_72 || $wnd.location.protocol == $intern_73);
    $wnd.__gwt_activeModules[$intern_4].canRedirect = devModePermitted;
    function supportsSessionStorage(){
      var key = $intern_74;
      try {
        $wnd.sessionStorage.setItem(key, key);
        $wnd.sessionStorage.removeItem(key);
        return true;
      }
       catch (e) {
        return false;
      }
    }

    if (devModePermitted && supportsSessionStorage()) {
      var devModeKey = $intern_75;
      var devModeUrl = $wnd.sessionStorage[devModeKey];
      if (!/^http:\/\/(localhost|127\.0\.0\.1)(:\d+)?\/.*$/.test(devModeUrl)) {
        if (devModeUrl && (window.console && console.log)) {
          console.log($intern_76 + devModeUrl);
        }
        devModeUrl = $intern_15;
      }
      if (devModeUrl && !$wnd[devModeKey]) {
        $wnd[devModeKey] = true;
        $wnd[devModeKey + $intern_77] = computeScriptBase();
        var devModeScript = $doc.createElement($intern_20);
        devModeScript.src = devModeUrl;
        var head = $doc.getElementsByTagName($intern_78)[$intern_7];
        head.insertBefore(devModeScript, head.firstElementChild || head.children[$intern_7]);
        return false;
      }
    }
  }
  loadExternalStylesheets();
  sendStats($intern_0, $intern_71);
  installScript(filename);
  return true;
}

showcase.succeeded = showcase();
