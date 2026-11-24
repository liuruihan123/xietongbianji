import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { QaCaseVO, QaCaseForm, QaCaseQuery, qaCaseVO } from '@/api/system/qaCase/types';

/**
 * 查询对话案例列表
 * @param query
 * @returns {*}
 */

export const listQaCase = (query?: QaCaseQuery): AxiosPromise<QaCaseVO[]> => {
  return request({
    url: '/system/qaCase/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询案例库数据列表
 * @param query
 * @returns {*}
 */

export const listTreeQaCase = (): AxiosPromise<qaCaseVO[]> => {
  return request({
    url: '/system/qaCase/getTreelist',
    method: 'get'
  });
};

/**
 * 查询对话案例详细
 * @param id
 */
export const getQaCase = (id: string | number): AxiosPromise<QaCaseVO> => {
  return request({
    url: '/system/qaCase/' + id,
    method: 'get'
  });
};

/**
 * 新增对话案例
 * @param data
 */
export const addQaCase = (data: QaCaseForm) => {
  return request({
    url: '/system/qaCase',
    method: 'post',
    data: data
  });
};

/**
 * 修改对话案例
 * @param data
 */
export const updateQaCase = (data: QaCaseForm) => {
  return request({
    url: '/system/qaCase',
    method: 'put',
    data: data
  });
};

/**
 * 删除对话案例
 * @param id
 */
export const delQaCase = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/qaCase/' + id,
    method: 'delete'
  });
};
