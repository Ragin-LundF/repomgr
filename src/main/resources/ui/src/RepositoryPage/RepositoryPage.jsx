import React from 'react';

import {versionService} from '../_services';
import ReactTable from "react-table";

class RepositoryPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            versions: [],
            page: {},
            artifactId: '',
            groupId: '',
            version: '',
            latestVersion: false,
            loading: false
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.fetchData = this.fetchData.bind(this);
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
            "version": this.state.version,
            "latestVersion": this.state.latestVersion
        };
        versionService.search(filter, sortField, sortDirection, page, size).then(versions => {
            this.setState({page: versions.page});
            return this.setState({versions: versions.versionInformations});
        });
    }

    fetchData(state, instance) {
        let sortField = '';
        let sortDirection = '';
        this.setState({ loading: true });
        if (state.sorted && state.sorted.length > 0) {
            sortField = state.sorted[0].id;
            if (state.sorted[0].desc) {
                sortDirection = 'DESC';
            } else {
                sortDirection = 'ASC';
            }

        }
        this.search(sortField, sortDirection, state.page, state.pageSize);
        this.setState({ loading: false });
    }

    render() {
        const { loading, page, versions } = this.state;
        return (
            <div className="col-lg-12">
                <div className="jumbotron">
                    <h1>Repository Manager</h1>
                    <p>Find the available packages here</p>
                    <form onSubmit={this.handleSubmit}>
                        <div className="row">
                            <div className="col-lg-4">
                                <div className="input-group">
                                    <span className="input-group-addon input-lg" id="basic-addon3">artifactId&nbsp;</span>
                                    <input type="text" className="form-control input-lg" name="artifactId" id="artifactId" aria-describedby="basic-addon3" value={this.state.artifactId} onChange={this.handleInputChange} />
                                </div>
                            </div>
                            <div className="col-lg-4">
                                <div className="input-group">
                                    <span className="input-group-addon input-lg" id="basic-addon3">groupId&nbsp;&nbsp;&nbsp;&nbsp;</span>
                                    <input type="text" className="form-control input-lg" name="groupId" id="groupId" aria-describedby="basic-addon3" value={this.state.groupId} onChange={this.handleInputChange} />
                                </div>
                            </div>
                            <div className="col-lg-4">
                                <div className="input-group">
                                    <span className="input-group-addon input-lg" id="basic-addon3">version&nbsp;&nbsp;&nbsp;&nbsp;</span>
                                    <input type="text" className="form-control input-lg" name="version" id="version" aria-describedby="basic-addon3" value={this.state.version} onChange={this.handleInputChange} />
                                </div>
                            </div>
                        </div>
                        <div className="row">
                            <div className="col-lg-12">&nbsp;</div>
                        </div>
                        <div className="row">
                            <div className="col-lg-12">
                                <div className="input-group">
                                    <span className="input-group-addon input-lg">
                                        <input type="checkbox" value={this.state.latestVersion} aria-label="latestVersion" id="latestVersion" name="latestVersion" onChange={this.handleInputChange} />
                                        <span className="input-lg" id="basic-addon3">Latest version only</span>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div className="row">
                            <div className="col-lg-12">&nbsp;</div>
                        </div>
                        <div className="row">
                            <div className="col-lg-12">
                                <div className="input-group">
                                    <button type="submit" className="btn btn-default btn-lg" aria-label="Left Align">
                                        <span className="glyphicon glyphicon-align-left" aria-hidden="true"> Filter</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
                <div className="panel panel-default">
                    <div className="panel-heading">
                        <h3>Available packages</h3>
                    </div>
                    <div className="panel-body">
                        {versions.length &&
                        <ReactTable
                            columns={[
                                {
                                    Header: "Project name",
                                    accessor: "projectName",
                                    id: "projectName",
                                },
                                {
                                    Header: "Group Id",
                                    accessor: "groupId",
                                    id: "groupId",
                                },
                                {
                                    Header: "Artifact Id",
                                    accessor: "artifactId",
                                    id: "artifactId",
                                },
                                {
                                    Header: "Version",
                                    accessor: "version",
                                    id: "version"
                                },
                                {
                                    Header: "Branch",
                                    accessor: "branch",
                                    id: "branch",
                                },
                                {
                                    Header: "Repository URL",
                                    accessor: "repositoryUrl",
                                    id: "repositoryUrl",
                                },
                                {
                                    Header: "Creation Date",
                                    accessor: "creationDate",
                                    id: "creationDate",
                                    Cell: row => (
                                        <span>
                                        {new Intl.DateTimeFormat('en-GB', {
                                                year: 'numeric',
                                                month: '2-digit',
                                                day: '2-digit',
                                                hour: '2-digit',
                                                hour12: false,
                                                minute: '2-digit'
                                            }).format(new Date(row.value))}
                                        </span>
                                    )
                                },
                            ]}
                            manual // Forces table not to paginate or sort automatically, so we can handle it server-side
                            data={versions}
                            pages={page.totalPages} // Display the total number of pages
                            loading={loading} // Display the loading overlay when we need it
                            onFetchData={this.fetchData} // Request new data when things change
                            defaultPageSize={10}
                            className="-striped -highlight"
                        />
                        }
                    </div>
                    <div className="panel-footer">
                        Page {page.currentPage} of {page.totalPages} with {page.numberOfElements} of {page.totalElements} elements.
                    </div>
                </div>
            </div>
        );
    }
}

export { RepositoryPage };