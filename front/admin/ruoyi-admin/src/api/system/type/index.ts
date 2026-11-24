import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { TypeVO, TypeForm, TypeQuery } from '@/api/system/type/types';

/**
 * 查询知识分类列表
 * @param query
 * @returns {*}
 */

export const listType = (query?: TypeQuery): AxiosPromise<TypeVO[]> => {
  return request({
    url: '/system/type/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询知识分类详细
 * @param id
 */
export const getType = (id: string | number): AxiosPromise<TypeVO> => {
  return request({
    url: '/system/type/' + id,
    method: 'get'
  });
};

/**
 * 新增知识分类
 * @param data
 */
export const addType = (data: TypeForm) => {
  return request({
    url: '/system/type',
    method: 'post',
    data: data
  });
};

/**
 * 修改知识分类
 * @param data
 */
export const updateType = (data: TypeForm) => {
  return request({
    url: '/system/type',
    method: 'put',
    data: data
  });
};

/**
 * 删除知识分类
 * @param id
 */
export const delType = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/type/' + id,
    method: 'delete'
  });
};
