import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ApyVO, ApyForm, ApyQuery } from '@/api/system/apy/types';

/**
 * 查询案例库列表
 * @param query
 * @returns {*}
 */

export const listApy = (query?: ApyQuery): AxiosPromise<ApyVO[]> => {
  return request({
    url: '/system/apy/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询案例库详细
 * @param id
 */
export const getApy = (id: string | number): AxiosPromise<ApyVO> => {
  return request({
    url: '/system/apy/' + id,
    method: 'get'
  });
};

/**
 * 新增案例库
 * @param data
 */
export const addApy = (data: ApyForm) => {
  return request({
    url: '/system/apy',
    method: 'post',
    data: data
  });
};

/**
 * 修改案例库
 * @param data
 */
export const updateApy = (data: ApyForm) => {
  return request({
    url: '/system/apy',
    method: 'put',
    data: data
  });
};

/**
 * 删除案例库
 * @param id
 */
export const delApy = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/apy/' + id,
    method: 'delete'
  });
};
