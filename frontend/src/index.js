import React from "react";
import ReactDOM from "react-dom";

import ArrowLeft from "./svg/arrow-left-short.svg";
import Gear from "./svg/gear-wide-connected.svg";

const LOADING = 0;
const LOADED = 1;

function Header(props) {
  return (
    <div className="p-3">
      <a href="/">
        <ArrowLeft className="fill-[#bbb] hover:fill-[#eee] hover:cursor-pointer" />
      </a>
    </div>
  );
}

class Settings extends React.Component {
  constructor(props) {
    super(props);
    this.state = { state: LOADING };
  }

  async componentDidMount() {
    if (this.state.state === LOADING) {
      const response = await fetch("/admin/settings.json");
      // @todo Check response.
      const responseJson = await response.json();

      this.setState({
        state: LOADED,
        defaultSearchEngine: responseJson.default_search_engine,
        searchEngines: responseJson.search_engines,
      });
    }
  }

  getDefaultSearchEngineList() {
    let searchEngineOptions = [];

    for (const searchEngine of this.state.searchEngines) {
      const isSelected = searchEngine.id === this.state.defaultSearchEngine;
      if (isSelected) {
        searchEngineOptions.push(
          <option value={searchEngine.id} selected>
            {searchEngine.name}
          </option>
        );
      } else {
        searchEngineOptions.push(
          <option value={searchEngine.id}>{searchEngine.name}</option>
        );
      }
    }

    return (
      <form>
        <select
          name="default-search-engine"
          id="default-search-engine"
          className="rounded border border-[#333] bg-[#222] w-64 h-8 px-2"
          onChange={(event) => this.updateDefaultSearchEngine(event)}
        >
          {searchEngineOptions}
        </select>
      </form>
    );
  }

  async updateDefaultSearchEngine(event) {
    this.setState({ defaultSearchEngine: event.target.value });
    const response = await fetch("/admin/settings.json", {
      method: "PUT",
      headers: new Headers({ "content-type": "application/json" }),
      body: JSON.stringify({
        default_search_engine: event.target.value,
      }),
    });
    // @todo Check response.
    const responseJson = await response.json();
  }

  render() {
    if (this.state.state === LOADING) {
      return <div className="bg-neutral-900 min-h-screen" />;
    } else {
      const defaultSearchEngineList = this.getDefaultSearchEngineList();

      return (
        <div className="bg-neutral-900 min-h-screen">
          <Header />
          <div className="flex flex-row text-neutral-200 w-full">
            <div className="px-5 flex flex-col w-1/4">
              <div className="flex flex-row px-2 py-1 rounded bg-[#222] space-x-2">
                <div className="flex items-center m-auto block max-w-fit">
                  <Gear />
                </div>
                <div className="flex flex-grow">General Settings</div>
              </div>
            </div>
            <div className="flex flex-col w-3/4 pl-5">
              <div className="py-2">Default Search Engine</div>
              {defaultSearchEngineList}
            </div>
          </div>
        </div>
      );
    }
  }
}

ReactDOM.render(<Settings />, document.getElementById("app"));
