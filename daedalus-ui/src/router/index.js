import React, { Component } from 'react';
import { Route, Switch } from 'react-router-dom';
import AssemblyLine from '../components/assemblyLine/index';
import WorkBench from '../components/workbench/index';
import MoreTable from '../components/workbench/moreTable';
import FilterTable from '../components/workbench/filterTable';
import Functions from '../components/function/index';
import EnvManage from '../components/manage/envManage';
import ExampleManage from '../components/manage/exampleManage';
import Iframe from '../components/useFile';
import TimingTrigger from '../components/timingTrigger';
import Catalog from '../components/catalog';

const adminRoutes = [{
  key: 'workbench',
  path: '/workbench',
  component: WorkBench
}, {
  key: 'assemblyLine',
  path: '/assemblyLine/:type',
  component: AssemblyLine
}, {
  key: 'moretable',
  path: '/moretable/:type',
  component: MoreTable
}, {
  key: 'filterTable',
  path: '/filterTable/people/:type',
  component: FilterTable
}, {
  key: 'filterTable1',
  path: '/filterTable/tag/:type',
  component: FilterTable
}, {
  key: 'function',
  path: '/function/:id',
  component: Functions
},{
  key: 'envManage',
  path: '/envManage',
  component: EnvManage
}, {
  key: 'exampleManage-mysql',
  path: '/exampleManage/mysql',
  component: ExampleManage
}, {
  key: 'exampleManage-redis',
  path: '/exampleManage/redis',
  component: ExampleManage
}, {
  key: 'exampleManage-http',
  path: '/exampleManage/http',
  component: ExampleManage
}, {
  key: 'exampleManage-es',
  path: '/exampleManage/es',
  component: ExampleManage
}, {
  key: 'exampleManage-registry',
  path: '/exampleManage/registry',
  component: ExampleManage
}, {
  key: 'useFile',
  path: '/useFile',
  component: Iframe
}, {
  key: 'timingTrigger',
  path: '/timingTrigger',
  component: TimingTrigger
}, {
  key: 'catalog',
  path: '/catalog',
  component: Catalog
}, {
  key: 'catalogShare',
  path: '/catalogshare/:id',
  component: Catalog
}, {
  key: 'home',
  path: '/',
  component: WorkBench
}];

class RouterConFig extends Component {

  render() {
    return (
      <div className="main-content">
        <Switch>
          {
            adminRoutes.map(item => (
              <Route path={item.path} component={item.component} key={item.key}></Route>
            ))
          }
        </Switch>
      </div>
    );
  }
}

export default RouterConFig;