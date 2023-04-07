<#ftl encoding="utf-8">
<#macro meta query>
<meta charset=UTF-8>
<meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
<meta name="viewport" content="width=device-width, initial_scale=1.0, user_scalabe=no"/>
<meta name="description" content="Argus is a privacy focused web search engine proxy"/>
<link
  rel="search"
  type="application/opensearchdescription+xml"
  title="Argus"
  href="/opensearch.xml"/>
<link rel="icon" type="image/svg+xml" href="/favicon.svg">
<title>Argus — ${query?truncate(20)}</title>
</#macro>

<#macro style>
html, body {
  min-height: 100%;
  width: 100%;
  margin: 0 !important;
  padding: 0 !important;
}

body {
  background-color: #171717;
}

.query {
  display: flex;
  justify-content: center;
  align-items: center;
  padding-top: 4px;
}

.query-line {
  display: flex;
  flex-direction: row;
}

.magnifying-glass {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40px;
  width: 40px;
  background-color: #505050;
  padding: 1px;
}

.textbox {
  border: 0px;
  border-radius: 0;
  border-image-width: 0;  
  height: 40px;
  margin-left: 0px;  
  padding-left: 10px;
  padding-right: 10px;
  padding-top: 1px; 
  padding-bottom: 1px;  
  outline: 0;
  font-size: 18px;
  background-color: #404040;
  color: #eeeeee;
}
</#macro>

<#macro script>
<script>
window.onload = function() {
    // Include user's time zone in search.
    const timeZoneField = document.getElementById("time-zone-field"); 
    const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone; 
    timeZoneField.value = timeZone;

    // Set cursor to end of text field.
    const textField = document.getElementById("text-field");
    textField.selectionStart = textField.value.length;
};
</script>
</#macro>

<#macro query query>
<div class="query">
  <form action="/query">
    <div class="query-line">
      <div class="magnifying-glass">
        <svg xmlns="http://www.w3.org/2000/svg" fill="#ccc"
        class="bi bi-search" viewBox="0 0 16 16" width="20" height="20">
          <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
        </svg>
      </div>
      <input id="time-zone-field" type="hidden" name="time_zone" value="" />
      <input id="text-field" class="textbox" type="text" value="${query}" name="q" title="" autofocus required>
    </div>        
  </form>
</div>
</#macro>