<#macro meta>
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
<title>Argus</title>
</#macro>

<#macro style>
<style>
html, body {
  min-height: 100%;
  overflow: hidden;
  width: 100%;
  position: fixed;
  margin: 0 !important;
  padding: 0 !important;
}

body {
  background-color: #171717;
}

.result {
  display: flex;
  justify-content: center; 
  color: #eeeeee;
  font-family: sans-serif;
  font-size: 18px;
  padding-top: 64px;
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
  height: 20px;
  width: 20px;
  background-color: #505050;
  padding: 11px;
}

.textbox {
  border: 0px; 
  height: 40px;
  padding-left: 10px;
  padding-right: 10px;
  outline: 0;
  font-size: 18px;
  background-color: #404040;
  color: #eeeeee;
}
</style>	
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
        class="bi bi-search" viewBox="0 0 16 16">
          <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
        </svg>
      </div>
      <input id="time-zone-field" type="hidden" name="time_zone" value="" />
      <input id="text-field" class="textbox" type="text" value="${query}" name="q" title="" autofocus required>
    </div>        
  </form>
</div>
</#macro>