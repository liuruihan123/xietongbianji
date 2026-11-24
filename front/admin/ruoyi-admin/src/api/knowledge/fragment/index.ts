import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { FragmentVO, FragmentForm, FragmentQuery } from '@/api/knowledge/fragment/types';

/**
 * 查询知识片段列表
 * @param query
 * @returns {*}
 */

export const listFragment = (query?: FragmentQuery): AxiosPromise<FragmentVO[]> => {
  return request({
    url: '/knowledge/fragment/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询知识片段详细
 * @param id
 */
export const getFragment = (id: string | number): AxiosPromise<FragmentVO> => {
  return request({
    url: '/knowledge/fragment/' + id,
    method: 'get'
  });
};

/**
 * 新增知识片段
 * @param data
 */
export const addFragment = (data: FragmentForm) => {
  return request({
    url: '/knowledge/fragment',
    method: 'post',
    data: data
  });
};

/**
 * 修改知识片段
 * @param data
 */
export const updateFragment = (data: FragmentForm) => {
  return request({
    url: '/knowledge/fragment',
    method: 'put',
    data: data
  });
};

/**
 * 删除知识片段
 * @param id
 */
export const delFragment = (id: string | number | Array<string | number>) => {
  return request({
    url: '/knowledge/fragment/' + id,
    method: 'delete'
  });
};
