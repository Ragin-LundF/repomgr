import React from 'react';

import {versionService} from '../_services';

class RepositoryPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            versions: [],
            page: {},
            artifactId: '',
            groupId: '',
            version: ''
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    componentDidMount() {
        this.search(null, null, null, null);
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        this.search(null, null, null, null);
    }

    search(sortField, sortDirection, page, size) {
        let filter = {
            "artifactId": this.state.artifactId,
            "groupId": this.state.groupId,
            "version": this.state.version
        };
        versionService.search(filter, sortField, sortDirection, page, size).then(versions => {
            this.setState({page: versions.page});
            return this.setState({versions: versions.versionInformations});
        });
    }

    render() {
        const { page, versions } = this.state;
        return (
            <div className="col-lg-12">
                <h1>Repository Manager</h1>
                <p>Find the available packages here</p>
                <h3>Available packages:</h3>
                {versions.length &&
                    <span>
                        <div>
                            <form onSubmit={this.handleSubmit}>
                                <label>
                                    ArtifactId:
                                    <input type="text" name="artifactId" value={this.state.artifactId} onChange={this.handleInputChange} />
                                </label>
                                <label>
                                    GroupId:
                                    <input type="text" name="groupId" value={this.state.groupId} onChange={this.handleInputChange} />
                                </label>
                                <label>
                                    Version:
                                    <input type="text" name="version" value={this.state.version} onChange={this.handleInputChange} />
                                </label>
                                <input type="submit" value="Filter" />
                            </form>
                        </div>
                        <div>
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
                        </div>
                        Page {page.currentPage} of {page.totalPages} with {page.numberOfElements} of {page.totalElements} elements.
                    </span>
                }
            </div>
        );
    }
}

export { RepositoryPage };