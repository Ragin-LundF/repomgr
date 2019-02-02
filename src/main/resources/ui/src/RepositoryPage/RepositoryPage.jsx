import React from 'react';

import {versionService} from '../_services';

class RepositoryPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            versions: []
        };
    }

    componentDidMount() {
        versionService.findAll().then(versions => {
            return this.setState({versions: versions.versionInformations});
        })
    }

    render() {
        const { versions } = this.state;
        return (
            <div className="col-lg-12">
                <h1>Repository Manager</h1>
                <p>Find the available packages here</p>
                <h3>Available packages:</h3>
                {versions.length &&
                    <table className="table">
                        <thead>
                            <tr>
                                <th scope="col">projectName</th>
                                <th scope="col">branch</th>
                                <th scope="col">groupId</th>
                                <th scope="col">artifactId</th>
                                <th scope="col">version</th>
                                <th scope="col">repositoryUrl</th>
                                <th scope="col">creationDate</th>
                            </tr>
                        </thead>
                        <tbody>
                        {versions.map((version, index) =>
                            <tr key={index}>
                                <td>{version.projectName}</td>
                                <td>{version.branch}</td>
                                <td>{version.groupId}</td>
                                <td>{version.artifactId}</td>
                                <td>{version.version}</td>
                                <td>{version.repositoryUrl}</td>
                                <td>
                                    {new Intl.DateTimeFormat('en-GB', {
                                        year: 'numeric',
                                        month: 'long',
                                        day: '2-digit',
                                        hour: '2-digit',
                                        hour12: false,
                                        minute: '2-digit'
                                    }).format(new Date(version.creationDate))}
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                }
            </div>
        );
    }
}

export { RepositoryPage };