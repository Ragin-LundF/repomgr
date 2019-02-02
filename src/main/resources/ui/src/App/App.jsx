import React from 'react';

import {RepositoryPage} from "../RepositoryPage";

class App extends React.Component {
    render() {
        return (
            <div className="jumbotron">
                <div className="container">
                    <div className="col-lg-12">
                        <RepositoryPage />
                    </div>
                </div>
            </div>
        );
    }
}

export { App };
