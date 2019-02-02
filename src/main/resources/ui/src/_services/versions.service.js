import config from 'config';
import { authHeader } from '../_helpers';

export const versionService = {
    findAll,
    search,
};

function search(filter) {
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ filter })
    };

    return fetch(`${config.apiUrl}/v1/repositories/search`, requestOptions)
        .then(handleResponse)
        .then(versions => {
            return versions;
        });
}

function findAll() {
    const requestOptions = {
        method: 'GET',
        // headers: authHeader()
    };

    return fetch(`${config.apiUrl}/v1/repositories`, requestOptions).then(handleResponse);
}

function handleResponse(response) {
    return response.text().then(text => {
        const data = text && JSON.parse(text);
        if (!response.ok) {
            if (response.status === 401) {
                // auto logout if 401 response returned from api
                logout();
                location.reload(true);
            }

            const error = (data && data.message) || response.statusText;
            return Promise.reject(error);
        }

        return data;
    });
}