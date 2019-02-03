import config from 'config';
// import { authHeader } from '../_helpers';

export const versionService = {
    search,
};

function search(filter, sortField, sortDirection, page, size) {
    if (filter == null) {
        filter = {}
    }
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(filter)
    };

    let urlParamObj = {
        "sortField": sortField,
        "sortDirection": sortDirection,
        "page": page,
        "size": size
    };
    let urlParams = Object.keys(urlParamObj).map((key) => {
        if (urlParamObj[key] != null) {
            return encodeURIComponent(key) + '=' + encodeURIComponent(urlParamObj[key])
        } else {
            return '';
        }
    }).join('&');

    return fetch(`${config.apiUrl}/v1/repositories/search?${urlParams}`, requestOptions)
        .then(handleResponse)
        .then(versions => {
            return versions;
        });
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