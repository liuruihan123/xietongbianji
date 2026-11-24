import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ContactusVO, ContactusForm, ContactusQuery } from '@/api/system/contactus/types';

/**
 * 查询contactus列表
 * @param query
 * @returns {*}
 */

export const listContactus = (query?: ContactusQuery): AxiosPromise<ContactusVO[]> => {
  return request({
    url: '/system/contactus/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询contactus详细
 * @param id
 */
export const getContactus = (id: string | number): AxiosPromise<ContactusVO> => {
  return request({
    url: '/system/contactus/' + id,
    method: 'get'
  });
};

/**
 * 新增contactus
 * @param data
 */
export const addContactus = (data: ContactusForm) => {
  return request({
    url: '/system/contactus',
    method: 'post',
    data: data
  });
};

/**
 * 修改contactus
 * @param data
 */
export const updateContactus = (data: ContactusForm) => {
  return request({
    url: '/system/contactus',
    method: 'put',
    data: data
  });
};

/**
 * 删除contactus
 * @param id
 */
export const delContactus = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/contactus/' + id,
    method: 'delete'
  });
};
